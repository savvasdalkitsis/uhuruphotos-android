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
package com.savvasdalkitsis.uhuruphotos.api.photos.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoImageSource
import kotlinx.coroutines.flow.Flow

interface PhotosUseCase {
    fun String?.toAbsoluteUrl(): String?
    fun String?.toThumbnailUrlFromIdNullable(): String?
    fun String.toThumbnailUrlFromId(isVideo: Boolean = false, imageSource: PhotoImageSource = PhotoImageSource.REMOTE): String
    fun String?.toFullSizeUrlFromIdNullable(isVideo: Boolean = false): String?
    fun String.toFullSizeUrlFromId(isVideo: Boolean = false, imageSource: PhotoImageSource = PhotoImageSource.REMOTE): String
    fun observeAllPhotoDetails(): Flow<List<PhotoDetails>>
    fun observePhotoDetails(id: String): Flow<PhotoDetails>
    fun observeFavouritePhotos(): Flow<Result<List<Photo>>>
    fun observeHiddenPhotos(): Flow<Result<List<Photo>>>
    suspend fun List<PhotoSummary>.mapToPhotos(): Result<List<Photo>>
    suspend fun getPhotoDetails(id: String, isVideo: Boolean, imageSource: PhotoImageSource):
            com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoDetails?
    suspend fun getFavouritePhotoSummaries(): Result<List<PhotoSummary>>
    suspend fun getFavouritePhotoSummariesCount(): Result<Long>
    suspend fun getHiddenPhotoSummaries(): List<PhotoSummary>
    suspend fun setPhotoFavourite(id: String, favourite: Boolean): Result<Unit>
    fun refreshDetails(id: String): Result<Unit>
    suspend fun refreshDetailsNowIfMissing(
        id: String,
        isVideo: Boolean = false,
        imageSource: PhotoImageSource = PhotoImageSource.REMOTE,
    ) : Result<Unit>
    suspend fun refreshDetailsNow(id: String, isVideo: Boolean, imageSource: PhotoImageSource) : Result<Unit>
    suspend fun refreshFavourites()
    suspend fun refreshHiddenPhotos()
    fun trashPhoto(id: String)
    fun deletePhoto(id: String)
    fun restorePhoto(id: String)
    fun downloadOriginal(id: String, video: Boolean)
    fun observeOriginalFileDownloadStatus(id: String): Flow<WorkInfo.State>
}