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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.FeedService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection.Complete
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection.Incomplete
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val database: Database,
    private val remoteMediaCollectionsQueries: RemoteMediaCollectionsQueries,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val feedService: FeedService,
) {

    suspend fun hasRemoteMediaCollections(): Boolean =
        remoteMediaCollectionsQueries.remoteMediaCollectionCount().awaitSingle() > 0

    fun observeRemoteMediaCollectionsByDate(
        feedFetchType: FeedFetchType,
        loadSmallInitialChunk: Boolean,
    ): Flow<Group<String, GetRemoteMediaCollections>> = flow {
        try {
            emitAll(remoteMediaCollectionsQueries.getRemoteMediaCollections(
                limit = if (loadSmallInitialChunk) 100 else -1,
                includeNoDates = feedFetchType.includeMediaWithoutDate,
                includeDates = feedFetchType.includeMediaWithDate,
                onlyVideos = feedFetchType.onlyVideos,
            ).asFlow()
                .mapToList(Dispatchers.IO).groupBy(GetRemoteMediaCollections::id)
                .distinctUntilChanged()
            )
        } catch (e: Exception) {
            log(e)
        }
    }

    suspend fun getRemoteMediaCollectionsByDate(
        feedFetchType: FeedFetchType,
    ): Group<String, GetRemoteMediaCollections> =
        remoteMediaCollectionsQueries.getRemoteMediaCollections(
            limit = -1,
            includeNoDates = feedFetchType.includeMediaWithoutDate,
            includeDates = feedFetchType.includeMediaWithDate,
            onlyVideos = feedFetchType.onlyVideos,
        ).awaitList()
            .groupBy(GetRemoteMediaCollections::id).let(::Group)

    suspend fun refreshRemoteMediaCollections(
        shallow: Boolean,
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    ): SimpleResult =
        remoteMediaUseCase.processRemoteMediaCollections(
            incompleteAlbumsFetcher = { feedService.getRemoteMediaCollectionsByDate().results },
            completeAlbumsFetcher = getCollectionAllPages(),
            shallow = shallow,
            onProgressChange = onProgressChange,
            incompleteAlbumsProcessor = { albums ->
                database.transaction {
                    remoteMediaCollectionsQueries.clearAll()
                    for (album in albums.map { it.toDbModel() }) {
                        remoteMediaCollectionsQueries.insert(album)
                    }
                }
            }
        )

    suspend fun refreshRemoteMediaCollection(collectionId: String) =
        remoteMediaUseCase.processRemoteMediaCollections(
            incompleteAlbumsFetcher = {
                val collections = remoteMediaCollectionsQueries.get(collectionId).awaitSingleOrNull()
                listOf(Incomplete(
                        id = collectionId,
                        date = collections?.date,
                        location = collections?.location.orEmpty(),
                        incomplete = true,
                        numberOfItems = collections?.numberOfItems ?: 0
                ))
            },
            completeAlbumsFetcher = getCollectionAllPages(),
            shallow = false,
            clearSummariesBeforeInserting = true,
            completeAlbumProcessor = { album ->
                remoteMediaCollectionsQueries.insert(album.toIncomplete().toDbModel())
            },
            incompleteAlbumsProcessor = { }
        )

    private fun getCollectionAllPages(): suspend (String) -> Complete = { id ->
        var page = 1
        val albums = mutableListOf<Complete>()
        do {
            val album = feedService.getRemoteMediaCollection(id, page).results
            albums += album
            page++
        } while (albums.sumOf { it.items.size } < album.numberOfItems)
        albums.reduce { acc, completeAlbum ->
            acc.copy(
                items = acc.items + completeAlbum.items
            )
        }
    }
}