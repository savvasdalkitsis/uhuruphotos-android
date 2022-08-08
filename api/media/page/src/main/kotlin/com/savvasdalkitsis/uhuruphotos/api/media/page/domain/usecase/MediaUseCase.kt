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
package com.savvasdalkitsis.uhuruphotos.api.media.page.domain.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSource
import kotlinx.coroutines.flow.Flow

interface MediaUseCase {

    fun String.toThumbnailUriFromId(isVideo: Boolean = false, mediaSource: MediaSource): String

    fun String.toFullSizeUriFromId(isVideo: Boolean = false, mediaSource: MediaSource): String

    fun observeLocalMedia(): Flow<MediaItemsOnDevice>

    fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDevice>

    fun observeFavouriteMedia(): Flow<Result<List<MediaItem>>>

    fun observeHiddenMedia(): Flow<Result<List<MediaItem>>>

    suspend fun List<DbRemoteMediaItemSummary>.mapToMediaItems(): Result<List<MediaItem>>

    suspend fun getMediaItemDetails(
        id: String,
        mediaSource: MediaSource
    ): MediaItemDetails?

    suspend fun getFavouriteMedia(): Result<List<MediaItem>>

    suspend fun getFavouriteMediaCount(): Result<Long>

    suspend fun getHiddenMedia(): Result<List<MediaItem>>

    suspend fun setMediaItemFavourite(id: String, favourite: Boolean): Result<Unit>

    suspend fun refreshDetailsNowIfMissing(
        id: String,
        isVideo: Boolean = false,
        mediaSource: MediaSource = MediaSource.REMOTE,
    ): Result<Unit>

    suspend fun refreshDetailsNow(
        id: String,
        isVideo: Boolean,
        mediaSource: MediaSource
    ): Result<Unit>

    suspend fun refreshFavouriteMedia()

    suspend fun refreshHiddenMedia()

    fun trashMediaItem(id: String)

    fun deleteMediaItem(id: String)

    fun restoreMediaItem(id: String)

    fun downloadOriginal(id: String, video: Boolean)

    fun observeOriginalFileDownloadStatus(id: String): Flow<WorkInfo.State>
}