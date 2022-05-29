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
package com.savvasdalkitsis.uhuruphotos.photos.usecase

import com.savvasdalkitsis.uhuruphotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.photos.repository.PhotoRepository
import com.savvasdalkitsis.uhuruphotos.photos.worker.PhotoWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotosUseCase @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val photoRepository: PhotoRepository,
    private val photoWorkScheduler: PhotoWorkScheduler,
    private val userUseCase: UserUseCase,
) {

    @JvmName("toAbsoluteUrlNull")
    fun String?.toAbsoluteUrl(): String? = this?.toAbsoluteUrl()

    fun String.toAbsoluteUrl(): String {
        val serverUrl = serverUseCase.getServerUrl()
        return this
            .removeSuffix(".webp")
            .removeSuffix(".mp4")
            .let { serverUrl + it }
    }

    @JvmName("toThumbnailUrlFromIdNull")
    fun String?.toThumbnailUrlFromId(): String? =
        this?.toThumbnailUrlFromId()

    fun String.toThumbnailUrlFromId(): String =
        "/media/square_thumbnails/$this".toAbsoluteUrl()

    fun String.toFullSizeUrlFromId(
        isVideo: Boolean = false,
    ) = when {
        isVideo -> "/media/video/$this".toAbsoluteUrl()
        else -> "/media/photos/$this".toAbsoluteUrl()
    }

    fun observeAllPhotoDetails(): Flow<List<PhotoDetails>> =
        photoRepository.observeAllPhotoDetails()

    fun observePhotoDetails(id: String): Flow<PhotoDetails> =
        photoRepository.observePhotoDetails(id)

    suspend fun getPhotoDetails(id: String): PhotoDetails? =
        photoRepository.getPhotoDetails(id)

    suspend fun setPhotoFavourite(id: String, favourite: Boolean) {
        userUseCase.getUserOrRefresh()?.let { user ->
            photoRepository.setPhotoRating(id, user.favoriteMinRating?.takeIf { favourite } ?: 0)
            photoWorkScheduler.schedulePhotoFavourite(id, favourite)
        }
    }

    fun refreshDetails(id: String) {
        photoRepository.refreshDetails(id)
    }

    suspend fun refreshDetailsNowIfMissing(id: String) {
        photoRepository.refreshDetailsNowIfMissing(id)
    }

    suspend fun refreshDetailsNow(id: String) {
        photoRepository.refreshDetailsNow(id)
    }

    fun deletePhoto(id: String) {
        photoWorkScheduler.schedulePhotoDeletion(id)
    }
}
