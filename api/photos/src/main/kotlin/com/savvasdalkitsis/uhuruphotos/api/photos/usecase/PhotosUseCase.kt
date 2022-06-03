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

import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import kotlinx.coroutines.flow.Flow

interface PhotosUseCase {
    fun String?.toAbsoluteUrl(): String?
    fun String?.toThumbnailUrlFromIdNullable(): String?
    fun String.toThumbnailUrlFromId(): String
    fun String.toFullSizeUrlFromId(isVideo: Boolean = false): String
    fun observeAllPhotoDetails(): Flow<List<PhotoDetails>>
    fun observePhotoDetails(id: String): Flow<PhotoDetails>
    suspend fun getPhotoDetails(id: String): PhotoDetails?
    suspend fun setPhotoFavourite(id: String, favourite: Boolean): Result<Unit>
    fun refreshDetails(id: String): Result<Unit>
    suspend fun refreshDetailsNowIfMissing(id: String) : Result<Unit>
    suspend fun refreshDetailsNow(id: String) : Result<Unit>
    fun deletePhoto(id: String)
}