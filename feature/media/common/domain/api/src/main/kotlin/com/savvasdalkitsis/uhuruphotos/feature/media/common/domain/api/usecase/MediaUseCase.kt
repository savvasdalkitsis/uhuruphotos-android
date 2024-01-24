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

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaFolderOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResult
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow

interface MediaUseCase {

    fun observeLocalMedia(): Flow<MediaItemsOnDevice>

    fun observeLocalAlbum(albumId: Int): Flow<MediaFolderOnDevice>

    fun observeFavouriteMedia(): Flow<Result<List<MediaItem>, Throwable>>

    fun observeHiddenMedia(): Flow<Result<List<MediaItem>, Throwable>>

    suspend fun List<DbRemoteMediaItemSummary>.mapToMediaItems(): Result<List<MediaItem>, Throwable>

    fun observeMediaItemDetails(id: MediaId<*>): Flow<MediaItemDetails>

    suspend fun getFavouriteMediaCount(): Result<Long, Throwable>

    suspend fun getHiddenMedia(): Result<List<MediaItem>, Throwable>

    suspend fun setMediaItemFavourite(id: MediaId<*>, favourite: Boolean): SimpleResult

    suspend fun refreshDetailsNowIfMissing(id: MediaId<*>): Result<MediaOperationResult, Throwable>

    suspend fun refreshDetailsNow(id: MediaId<*>): SimpleResult

    suspend fun refreshFavouriteMedia(): SimpleResult

    suspend fun refreshHiddenMedia(): SimpleResult

    fun trashMediaItem(id: MediaId<*>)

    fun restoreMediaItem(id: MediaId<*>)

    suspend fun List<MediaCollectionSource>.toMediaCollections(): List<MediaCollection>

    suspend fun Group<String, MediaCollectionSource>.toMediaCollection(): List<MediaCollection>
}