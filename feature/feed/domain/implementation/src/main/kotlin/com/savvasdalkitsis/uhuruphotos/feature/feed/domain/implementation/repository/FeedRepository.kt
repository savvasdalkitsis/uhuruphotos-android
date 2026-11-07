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
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingle
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaCollectionsFilterModel
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
        val filter = RemoteMediaCollectionsFilterModel.Timeline.copy(modifiedSince = lastFeedRefresh)
        return remoteMediaUseCase.processRemoteMediaCollections(
            remoteMediaDaySummaryResponseAlbumsFetcher = {
                remoteMediaUseCase.getRemoteMediaCollections(filter)
            },
            remoteMediaDayCompleteResponseAlbumsFetcher = { id ->
                remoteMediaUseCase.getRemoteMediaCollection(id, filter)
            },
            onProgressChange = onProgressChange,
            clearSummariesBeforeInserting = true,
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
                listOf(localCollectionSummary(collectionId))
            },
            remoteMediaDayCompleteResponseAlbumsFetcher = { id ->
                remoteMediaUseCase.getRemoteMediaCollection(id, RemoteMediaCollectionsFilterModel.Timeline)
            },
            clearSummariesBeforeInserting = true,
        )

    private suspend fun localCollectionSummary(collectionId: String): RemoteMediaDaySummaryResponse =
        remoteMediaCollectionsQueries.get(collectionId).awaitSingleOrNull()?.toDaySummary()
            ?: RemoteMediaDaySummaryResponse(id = collectionId, date = null, numberOfItems = 0)

    private fun DbRemoteMediaCollections.toDaySummary() = RemoteMediaDaySummaryResponse(
        id = id,
        date = date,
        location = location,
        incomplete = true,
        numberOfItems = numberOfItems,
    )

    fun invalidateRemoteFeed() {
        setLastFeedRefresh(null)
    }
}