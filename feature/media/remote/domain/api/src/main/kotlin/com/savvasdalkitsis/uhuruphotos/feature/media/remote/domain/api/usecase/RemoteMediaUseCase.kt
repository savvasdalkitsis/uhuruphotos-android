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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import kotlinx.coroutines.flow.Flow

interface RemoteMediaUseCase {

    fun observeAllRemoteMediaDetails(): Flow<List<DbRemoteMediaItemDetails>>

    fun observeFavouriteRemoteMedia(): Flow<Result<List<DbRemoteMediaItemSummary>>>

    suspend fun observeRemoteMediaItemDetails(id: String): Flow<DbRemoteMediaItemDetails>

    suspend fun getRemoteMediaItemDetails(id: String): DbRemoteMediaItemDetails?

    fun observeHiddenRemoteMedia(): Flow<List<DbRemoteMediaItemSummary>>

    suspend fun getFavouriteMediaSummaries(): Result<List<DbRemoteMediaItemSummary>>

    suspend fun getFavouriteMediaSummariesCount(): Result<Long>

    suspend fun getHiddenMediaSummaries(): List<DbRemoteMediaItemSummary>

    suspend fun setMediaItemFavourite(id: String, favourite: Boolean): Result<Unit>

    suspend fun refreshDetailsNowIfMissing(id: String): Result<Unit>

    suspend fun refreshDetailsNow(id: String): Result<Unit>

    suspend fun refreshFavouriteMedia(): Result<Unit>

    suspend fun refreshHiddenMedia(): Result<Unit>

    fun trashMediaItem(id: String)

    fun deleteMediaItems(vararg ids: String)

    fun restoreMediaItem(id: String)

    suspend fun downloadThumbnail(id: MediaId.Remote)

    suspend fun processRemoteMediaCollections(
        albumsFetcher: suspend () -> RemoteMediaCollectionsByDate,
        remoteMediaCollectionFetcher: suspend (String) -> RemoteMediaCollection.Complete,
        shallow: Boolean,
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
        incompleteAlbumsProcessor: suspend (List<RemoteMediaCollection.Incomplete>) -> Unit = {},
        completeAlbumProcessor: suspend (RemoteMediaCollection.Complete) -> Unit = {},
        clearSummariesBeforeInserting: Boolean = true,
    ): Result<Unit>
}