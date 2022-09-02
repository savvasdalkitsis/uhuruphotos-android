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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import kotlinx.coroutines.flow.Flow

interface MediaUseCase {

    fun MediaId<*>.toThumbnailUriFromId(isVideo: Boolean = false): String

    fun MediaId<*>.toFullSizeUriFromId(isVideo: Boolean = false): String

    fun observeLocalMedia(): Flow<MediaItemsOnDevice>

    fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDevice>

    fun observeFavouriteMedia(): Flow<Result<List<MediaItem>>>

    fun observeHiddenMedia(): Flow<Result<List<MediaItem>>>

    suspend fun List<DbRemoteMediaItemSummary>.mapToMediaItems(): Result<List<MediaItem>>

    suspend fun getMediaItemDetails(id: MediaId<*>): MediaItemDetails?

    suspend fun getFavouriteMedia(): Result<List<MediaItem>>

    suspend fun getFavouriteMediaCount(): Result<Long>

    suspend fun getHiddenMedia(): Result<List<MediaItem>>

    suspend fun setMediaItemFavourite(id: MediaId<*>, favourite: Boolean): Result<Unit>

    suspend fun refreshDetailsNowIfMissing(id: MediaId<*>, isVideo: Boolean = false): Result<Unit>

    suspend fun refreshDetailsNow(id: MediaId<*>, isVideo: Boolean): Result<Unit>

    suspend fun refreshFavouriteMedia(): Result<Unit>

    suspend fun refreshHiddenMedia(): Result<Unit>

    fun trashMediaItem(id: MediaId<*>)

    fun deleteMediaItem(id: MediaId<*>)

    fun restoreMediaItem(id: MediaId<*>)

    fun downloadOriginal(id: MediaId<*>, video: Boolean)

    fun observeOriginalFileDownloadStatus(id: MediaId<*>): Flow<WorkInfo.State>

    suspend fun Group<String, MediaCollectionSource>.toMediaCollection(): List<MediaCollection>
}