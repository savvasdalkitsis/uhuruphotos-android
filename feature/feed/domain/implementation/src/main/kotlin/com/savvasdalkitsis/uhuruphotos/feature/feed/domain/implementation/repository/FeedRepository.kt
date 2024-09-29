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
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.http.FeedService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDayCompleteResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDaySummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class FeedRepository @Inject constructor(
    private val database: Database,
    private val remoteMediaCollectionsQueries: RemoteMediaCollectionsQueries,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val feedService: FeedService,
    @PlainTextPreferences
    private val preferences: Preferences,
    @DateModule.ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateTimeFormatter,
) {

    private val lastFeedRefresh: String? get() =
        preferences.getNullableString("lastFeedRefresh", null)
    private fun setLastFeedRefresh(value: String?) {
        when {
            value != null -> preferences.set("lastFeedRefresh", value)
            else -> preferences.remove("lastFeedRefresh")
        }
    }

    suspend fun hasRemoteMediaCollections(): Boolean =
        remoteMediaCollectionsQueries.remoteMediaCollectionCount().awaitSingle() > 0

    fun observeRemoteMediaCollectionsByDate(
        feedFetchTypeModel: FeedFetchTypeModel,
        loadSmallInitialChunk: Boolean,
    ): Flow<Group<String, GetRemoteMediaCollections>> = flow {
        try {
            emitAll(remoteMediaCollectionsQueries.getRemoteMediaCollections(
                limit = if (loadSmallInitialChunk) 100 else -1,
                includeNoDates = feedFetchTypeModel.includeMediaWithoutDate,
                includeDates = feedFetchTypeModel.includeMediaWithDate,
                onlyVideos = feedFetchTypeModel.onlyVideos,
            ).asFlow()
                .mapToList(Dispatchers.IO).groupBy(GetRemoteMediaCollections::id)
                .distinctUntilChanged()
            )
        } catch (e: Exception) {
            log(e)
        }
    }

    suspend fun getRemoteMediaCollectionsByDate(
        feedFetchTypeModel: FeedFetchTypeModel,
    ): Group<String, GetRemoteMediaCollections> =
        remoteMediaCollectionsQueries.getRemoteMediaCollections(
            limit = -1,
            includeNoDates = feedFetchTypeModel.includeMediaWithoutDate,
            includeDates = feedFetchTypeModel.includeMediaWithDate,
            onlyVideos = feedFetchTypeModel.onlyVideos,
        ).awaitList()
            .groupBy(GetRemoteMediaCollections::id).let(::Group)

    suspend fun refreshRemoteFeed(
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    ): SimpleResult {
        val start = parsingDateTimeFormat.print(Instant.now().toDateTime(DateTimeZone.UTC))
        return remoteMediaUseCase.processRemoteMediaCollections(
            remoteMediaDaySummaryResponseAlbumsFetcher = {
                when (val last = lastFeedRefresh) {
                    null -> feedService.getRemoteFeed().results
                    else -> feedService.getRemoteFeedSince(last).results
                }
            },
            remoteMediaDayCompleteResponseAlbumsFetcher = getCollectionAllPages(lastFeedRefresh),
            onProgressChange = onProgressChange,
            incompleteAlbumsProcessor = { albums ->
                database.transaction {
                    for (album in albums.map { it.toDbModel() }) {
                        remoteMediaCollectionsQueries.insert(album)
                    }
                }
            },
        ).onSuccess {
            setLastFeedRefresh(start)
        }
    }

    suspend fun refreshRemoteMediaCollection(collectionId: String) =
        remoteMediaUseCase.processRemoteMediaCollections(
            remoteMediaDaySummaryResponseAlbumsFetcher = {
                val collections = remoteMediaCollectionsQueries.get(collectionId).awaitSingleOrNull()
                listOf(
                    RemoteMediaDaySummaryResponse(
                        id = collectionId,
                        date = collections?.date,
                        location = collections?.location.orEmpty(),
                        incomplete = true,
                        numberOfItems = collections?.numberOfItems ?: 0
                )
                )
            },
            remoteMediaDayCompleteResponseAlbumsFetcher = getCollectionAllPages(),
            clearSummariesBeforeInserting = true,
        )

    private fun getCollectionAllPages(since: String? = null): suspend (String) -> RemoteMediaDayCompleteResponse = { id ->
        var page = 1
        val albums = mutableListOf<RemoteMediaDayCompleteResponse>()
        do {
            val album = when(since) {
                null -> feedService.getRemoteFeedDay(id, page).results
                else -> feedService.getRemoteFeedDaySince(id, page, since).results
            }
            albums += album
            page++
        } while (albums.sumOf { it.items.size } < album.numberOfItems)
        albums.reduce { acc, completeAlbum ->
            acc.copy(
                items = acc.items + completeAlbum.items
            )
        }
    }

    fun invalidateRemoteFeed() {
        setLastFeedRefresh(null)
    }
}