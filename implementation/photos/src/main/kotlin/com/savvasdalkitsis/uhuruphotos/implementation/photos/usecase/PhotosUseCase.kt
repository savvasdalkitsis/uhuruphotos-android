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

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.log.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.mediastore.usecase.MediaStoreContentUriResolver
import com.savvasdalkitsis.uhuruphotos.api.mediastore.usecase.MediaStoreUseCase
import com.savvasdalkitsis.uhuruphotos.api.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource.LOCAL
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource.REMOTE
import com.savvasdalkitsis.uhuruphotos.api.photos.model.deserializePeopleNames
import com.savvasdalkitsis.uhuruphotos.api.photos.model.latLng
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
    private val mediaStoreUseCase: MediaStoreUseCase,
    private val dateDisplayer: DateDisplayer,
    private val peopleUseCase: PeopleUseCase,
) : PhotosUseCase {

    override fun String?.toAbsoluteUrl(): String? = this?.toAbsoluteRemoteUrl()

    @JvmName("toAbsoluteUrlNotNull")
    private fun String.toAbsoluteRemoteUrl(): String {
        val serverUrl = serverUseCase.getServerUrl()
        return this
            .removeSuffix(".webp")
            .removeSuffix(".mp4")
            .let { serverUrl + it }
    }

    override fun String?.toThumbnailUrlFromIdNullable(): String? =
        this?.toThumbnailUrlFromId()

    override fun String.toThumbnailUrlFromId(isVideo: Boolean, imageSource: PhotoImageSource): String = when (imageSource) {
        REMOTE -> "/media/square_thumbnails/$this".toAbsoluteRemoteUrl()
        LOCAL -> contentUri(isVideo)
    }

    override fun String?.toFullSizeUrlFromIdNullable(isVideo: Boolean): String? =
        this?.toFullSizeUrlFromId(isVideo)

    override fun String.toFullSizeUrlFromId(isVideo: Boolean, imageSource: PhotoImageSource): String = when (imageSource) {
        REMOTE -> when {
            isVideo -> "/media/video/$this".toAbsoluteRemoteUrl()
            else -> "/media/photos/$this".toAbsoluteRemoteUrl()
        }
        LOCAL -> contentUri(isVideo)
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

    override suspend fun getPhotoDetails(
        id: String,
        isVideo: Boolean,
        imageSource: PhotoImageSource
    ): com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoDetails? = when (imageSource) {
        REMOTE -> photoRepository.getPhotoDetails(id)?.toPhotoDetails()
        else -> mediaStoreUseCase.getItem(id.toInt(), isVideo)?.let {
            com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoDetails(
                formattedDateAndTime = it.dateTaken,
                isFavourite = false,
                isVideo = isVideo,
                location = "",
                latLon = it.latLon?.let { (lat, lon) -> LatLon(lat, lon) },
                path = it.contentUri,
                peopleInPhoto = emptyList(),
            )
        }
    }

    private suspend fun PhotoDetails.toPhotoDetails(): com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoDetails {
        val favouriteThreshold = userUseCase.getUserOrRefresh().getOrNull()?.favoriteMinRating
        val people = peopleUseCase.getPeopleByName().ifEmpty {
            peopleUseCase.refreshPeople()
            peopleUseCase.getPeopleByName()
        }
        val peopleNamesList = peopleNames.orEmpty().deserializePeopleNames
        val peopleInPhoto = people
            .filter { it.name in peopleNamesList }
            .map {
                it.toPerson { person ->
                    person.toAbsoluteUrl()
                }
            }
        return com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoDetails(
            formattedDateAndTime = dateDisplayer.dateTimeString(timestamp),
            isFavourite = favouriteThreshold != null && (rating ?: 0) >= favouriteThreshold,
            isVideo = video == true,
            location = location.orEmpty(),
            latLon = latLng,
            path = imagePath,
            peopleInPhoto = peopleInPhoto,
        )
    }

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

    override suspend fun refreshDetailsNowIfMissing(
        id: String,
        isVideo: Boolean,
        imageSource: PhotoImageSource
    ) : Result<Unit> = runCatchingWithLog {
        when (imageSource) {
            REMOTE -> photoRepository.refreshDetailsNowIfMissing(id)
            LOCAL -> {
                if (mediaStoreUseCase.getItem(id.toInt(), isVideo) == null) {
                    mediaStoreUseCase.refreshItem(id.toInt(), isVideo)
                }
            }
        }
    }

    override suspend fun refreshDetailsNow(
        id: String,
        isVideo: Boolean,
        imageSource: PhotoImageSource
    ) : Result<Unit> = runCatchingWithLog {
        when (imageSource) {
            REMOTE -> photoRepository.refreshDetailsNow(id)
            LOCAL -> mediaStoreUseCase.refreshItem(id.toInt(), isVideo)
        }
    }

    override suspend fun refreshFavourites() {
        withFavouriteThreshold {
            photoRepository.refreshFavourites(it)
        }
    }

    override fun downloadOriginal(id: String, video: Boolean) {
        photoWorkScheduler.schedulePhotoOriginalFileRetrieve(id, video)
    }

    override fun observeOriginalFileDownloadStatus(id: String): Flow<WorkInfo.State> =
        photoWorkScheduler.observePhotoOriginalFileRetrieveJobStatus(id)

    override suspend fun refreshHiddenPhotos() {
        photoRepository.refreshHidden()
    }

    override fun trashPhoto(id: String) {
        photoWorkScheduler.schedulePhotoTrashing(id)
    }

    override fun deletePhoto(id: String) {
        photoWorkScheduler.schedulePhotoDeletion(id)
    }

    override fun restorePhoto(id: String) {
        photoWorkScheduler.schedulePhotoRestoration(id)
    }

    private suspend fun <T> withFavouriteThreshold(action: suspend (Int) -> T): Result<T> =
        userUseCase.getUserOrRefresh().mapCatching {
            action(it.favoriteMinRating!!)
        }

    private fun String.contentUri(isVideo: Boolean) =
        MediaStoreContentUriResolver.getContentUriForItem(toLong(), isVideo).toString()
}
