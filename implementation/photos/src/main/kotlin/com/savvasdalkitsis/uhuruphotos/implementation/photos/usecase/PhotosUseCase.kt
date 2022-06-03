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

import com.savvasdalkitsis.uhuruphotos.api.log.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.photos.repository.PhotoRepository
import com.savvasdalkitsis.uhuruphotos.implementation.photos.worker.PhotoWorkScheduler
import kotlinx.coroutines.flow.Flow
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

    override fun String.toFullSizeUrlFromId(isVideo: Boolean): String = when {
        isVideo -> "/media/video/$this".toAbsoluteUrl()
        else -> "/media/photos/$this".toAbsoluteUrl()
    }

    override fun observeAllPhotoDetails(): Flow<List<PhotoDetails>> =
        photoRepository.observeAllPhotoDetails()

    override fun observePhotoDetails(id: String): Flow<PhotoDetails> =
        photoRepository.observePhotoDetails(id)

    override suspend fun getPhotoDetails(id: String): PhotoDetails? =
        photoRepository.getPhotoDetails(id)

    override suspend fun setPhotoFavourite(id: String, favourite: Boolean): Result<Unit> = runCatchingWithLog {
        userUseCase.getUserOrRefresh()?.let { user ->
            photoRepository.setPhotoRating(id, user.favoriteMinRating?.takeIf { favourite } ?: 0)
            photoWorkScheduler.schedulePhotoFavourite(id, favourite)
        }
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

    override fun deletePhoto(id: String) {
        photoWorkScheduler.schedulePhotoDeletion(id)
    }
}
