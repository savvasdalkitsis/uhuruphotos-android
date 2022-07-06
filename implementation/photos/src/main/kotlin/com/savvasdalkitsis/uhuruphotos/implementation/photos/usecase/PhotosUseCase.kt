/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.implementation.photos.usecase

import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.log.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.photos.repository.PhotoRepository
import com.savvasdalkitsis.uhuruphotos.implementation.photos.worker.PhotoWorkScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotosUseCase @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val photoRepository: PhotoRepository,
    private val photoWorkScheduler: PhotoWorkScheduler,
    private val userUseCase: UserUseCase,
) : PhotosUseCase {

    override fun String?.toAbsoluteUrl(): String? = this?.toAbsoluteUrl()

    @JvmName("toAbsoluteUrlNotNull")
    private fun String.toAbsoluteUrl(): String {
        val serverUrl = serverUseCase.getServerUrl()
        return this
            .removeSuffix(".webp")
            .removeSuffix(".mp4")
            .let { serverUrl + it }
    }

    override fun String?.toThumbnailUrlFromIdNullable(): String? =
        this?.toThumbnailUrlFromId()

    override fun String.toThumbnailUrlFromId(): String =
        "/media/square_thumbnails/$this".toAbsoluteUrl()

    override fun String?.toFullSizeUrlFromIdNullable(isVideo: Boolean): String? =
        this?.toFullSizeUrlFromId(isVideo)

    override fun String.toFullSizeUrlFromId(isVideo: Boolean): String = when {
        isVideo -> "/media/video/$this".toAbsoluteUrl()
        else -> "/media/photos/$this".toAbsoluteUrl()
    }

    override fun observeAllPhotoDetails(): Flow<List<PhotoDetails>> =
        photoRepository.observeAllPhotoDetails()

    override fun observePhotoDetails(id: String): Flow<PhotoDetails> =
        photoRepository.observePhotoDetails(id)

    override fun observeFavouritePhotos(): Flow<Result<List<Photo>>> =
        flow {
            emitAll(
                withFavouriteThreshold { threshold ->
                    photoRepository.observeFavouritePhotos(threshold)
                }.mapCatching { flow ->
                    flow.map { photoSummaries ->
                        photoSummaries.mapToPhotos()
                    }
                }.getOrElse { flowOf(Result.failure(it)) }
            )
        }

    override fun observeHiddenPhotos(): Flow<Result<List<Photo>>> =
            photoRepository.observeHiddenPhotos()
                .mapToPhotos()

    private fun Flow<List<PhotoSummary>>.mapToPhotos(): Flow<Result<List<Photo>>> =
        map { it.mapToPhotos() }

    override suspend fun List<PhotoSummary>.mapToPhotos(): Result<List<Photo>> =
        withFavouriteThreshold { threshold ->
            map {
                Photo(
                    id = it.id,
                    thumbnailUrl = it.id.toThumbnailUrlFromIdNullable(),
                    fullResUrl = it.id.toFullSizeUrlFromId(it.isVideo),
                    fallbackColor = it.dominantColor,
                    isFavourite = (it.rating ?: 0) >= threshold,
                    ratio = it.aspectRatio ?: 1f,
                    isVideo = it.isVideo,
                )
            }
        }

    override suspend fun getPhotoDetails(id: String): PhotoDetails? =
        photoRepository.getPhotoDetails(id)

    override suspend fun getFavouritePhotoSummaries(): Result<List<PhotoSummary>> =
        withFavouriteThreshold {
            photoRepository.getFavouritePhotos(it)
        }

    override suspend fun getFavouritePhotoSummariesCount(): Result<Long> = withFavouriteThreshold {
        photoRepository.getFavouritePhotosCount(it)
    }

    override suspend fun getHiddenPhotoSummaries(): List<PhotoSummary> =
        photoRepository.getHiddenPhotos()

    override suspend fun setPhotoFavourite(id: String, favourite: Boolean): Result<Unit> =
        userUseCase.getUserOrRefresh().mapCatching { user ->
            photoRepository.setPhotoRating(id, user.favoriteMinRating?.takeIf { favourite } ?: 0)
            photoWorkScheduler.schedulePhotoFavourite(id, favourite)
        }

    override fun refreshDetails(id: String): Result<Unit> = runCatchingWithLog {
        photoRepository.refreshDetails(id)
    }

    override suspend fun refreshDetailsNowIfMissing(id: String) : Result<Unit> = runCatchingWithLog {
        photoRepository.refreshDetailsNowIfMissing(id)
    }

    override suspend fun refreshDetailsNow(id: String) : Result<Unit> = runCatchingWithLog {
        photoRepository.refreshDetailsNow(id)
    }

    override suspend fun refreshFavourites() {
        withFavouriteThreshold {
            photoRepository.refreshFavourites(it)
        }
    }

    override suspend fun refreshHiddenPhotos() {
        photoRepository.refreshHidden()
    }

    override fun deletePhoto(id: String) {
        photoWorkScheduler.schedulePhotoDeletion(id)
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T> =
        userUseCase.getUserOrRefresh().mapCatching {
            action(it.favoriteMinRating!!)
        }
}
