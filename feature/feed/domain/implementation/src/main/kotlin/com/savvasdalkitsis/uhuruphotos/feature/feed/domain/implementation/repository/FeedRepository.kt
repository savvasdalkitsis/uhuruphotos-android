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

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.await
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.FeedService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.RemoteMediaCollectionsByDate
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val remoteMediaCollectionsQueries: RemoteMediaCollectionsQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val feedService: FeedService,
) {

    private var allRemoteMediaCollections: Group<String, GetRemoteMediaCollections> = Group(emptyMap())

    suspend fun hasRemoteMediaCollections(): Boolean =
        remoteMediaCollectionsQueries.remoteMediaCollectionCount().awaitSingle() > 0

    fun observeRemoteMediaCollectionsByDate(): Flow<Group<String, GetRemoteMediaCollections>> =
        remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = -1).asFlow()
            .onStart {
                if (allRemoteMediaCollections.items.isEmpty()) {
                    emitAll(remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = 100).asFlow().take(1))
                }
            }
            .mapToList().groupBy(GetRemoteMediaCollections::id)
            .onStart {
                if (allRemoteMediaCollections.items.isNotEmpty()) {
                    emit(allRemoteMediaCollections)
                }
            }
            .onEach {
                allRemoteMediaCollections = it
            }
            .distinctUntilChanged()


    suspend fun getRemoteMediaCollectionsByDate(): Group<String, GetRemoteMediaCollections> =
        remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = -1).await().groupBy(GetRemoteMediaCollections::id).let(::Group)

    suspend fun refreshRemoteMediaCollections(
        shallow: Boolean,
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    ): Result<Unit> =
        remoteMediaUseCase.processRemoteMediaCollections(
            albumsFetcher = { feedService.getRemoteMediaCollectionsByDate() },
            remoteMediaCollectionFetcher = getCollectionAllPages(),
            shallow = shallow,
            onProgressChange = onProgressChange,
            incompleteAlbumsProcessor = { albums ->
                remoteMediaCollectionsQueries.transaction {
                    remoteMediaCollectionsQueries.clearAll()
                    for (album in albums.map { it.toDbModel() }) {
                        remoteMediaCollectionsQueries.insert(album)
                    }
                }
            }
        )

    suspend fun refreshRemoteMediaCollection(collectionId: String) {
        remoteMediaUseCase.processRemoteMediaCollections(
            albumsFetcher = { RemoteMediaCollectionsByDate(
                results = listOf(RemoteMediaCollection.Incomplete(collectionId, null, "", true, 1))
            ) },
            remoteMediaCollectionFetcher = getCollectionAllPages(),
            shallow = false,
            incompleteAlbumsProcessor = { albums ->
                remoteMediaCollectionsQueries.transaction {
                    remoteMediaItemSummaryQueries.deletePhotoSummariesforAlbum(collectionId)
                    for (album in albums.map { it.toDbModel() }) {
                        remoteMediaCollectionsQueries.insert(album)
                    }
                }
            }
        )
    }

    private fun getCollectionAllPages(): suspend (String) -> RemoteMediaCollection.Complete = { id ->
        var page = 1
        val albums = mutableListOf<RemoteMediaCollection.Complete>()
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