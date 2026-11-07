/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaCollectionsFilterModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDayCompleteResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDaySummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.RemoteMediaCollectionsService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.toDaySummary
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteMediaCollectionsRepository @Inject constructor(
    private val service: RemoteMediaCollectionsService,
    private val remoteMediaCollectionsQueries: RemoteMediaCollectionsQueries,
) {

    @Volatile
    private var legacyApi = false

    suspend fun getCollections(
        filter: RemoteMediaCollectionsFilterModel,
    ): List<RemoteMediaDaySummaryResponse> = when {
        legacyApi -> onNotFound({ legacyCollections(filter) }) {
            legacyApi = false
            days(filter)
        }
        else -> onNotFound({ days(filter) }) {
            legacyApi = true
            legacyCollections(filter)
        }
    }

    suspend fun getCollection(
        id: String,
        filter: RemoteMediaCollectionsFilterModel,
    ): RemoteMediaDayCompleteResponse = when {
        legacyApi -> onNotFound({ legacyDay(id, filter) }) { emptyDay(id) }
        else -> onNotFound({ day(id, filter) }) {
            onNotFound({ legacyDay(id, filter).also { legacyApi = true } }) { emptyDay(id) }
        }
    }

    private suspend fun days(filter: RemoteMediaCollectionsFilterModel) =
        service.getDays(filter.toQuery()).results
            .map { it.toDaySummary() }
            .filter { filter.modifiedSince == null || it.changedLocally() }

    private suspend fun RemoteMediaDaySummaryResponse.changedLocally(): Boolean =
        remoteMediaCollectionsQueries.get(id).awaitSingleOrNull()?.numberOfItems != numberOfItems

    private suspend fun legacyCollections(filter: RemoteMediaCollectionsFilterModel) =
        service.getLegacyDays(filter.toLegacyQuery()).results

    private suspend fun day(
        id: String,
        filter: RemoteMediaCollectionsFilterModel,
    ): RemoteMediaDayCompleteResponse {
        val items = mutableListOf<RemoteMediaItemSummaryResponse>()
        var after: String? = null
        do {
            val page = service.getDayPhotos(id, after, DAY_PAGE_SIZE, filter.toQuery())
            items += page.items
            after = page.next
        } while (after != null)
        return emptyDay(id).copy(numberOfItems = items.size, items = items)
    }

    private suspend fun legacyDay(
        id: String,
        filter: RemoteMediaCollectionsFilterModel,
    ): RemoteMediaDayCompleteResponse {
        val query = filter.toLegacyQuery()
        val first = service.getLegacyDay(id, 1, query).results
        val items = first.items.toMutableList()
        var page = 1
        var lastPageSize = first.items.size
        while (lastPageSize > 0 && items.size < first.numberOfItems) {
            page++
            val next = service.getLegacyDay(id, page, query).results.items
            items += next
            lastPageSize = next.size
        }
        return first.copy(items = items)
    }

    private fun emptyDay(id: String) = RemoteMediaDayCompleteResponse(
        id = id,
        date = null,
        numberOfItems = 0,
        items = emptyList(),
    )

    private suspend fun <T> onNotFound(
        action: suspend () -> T,
        fallback: suspend () -> T,
    ): T = try {
        action()
    } catch (e: HttpException) {
        if (e.code() != HTTP_NOT_FOUND) throw e
        fallback()
    }

    private companion object {
        const val DAY_PAGE_SIZE = 500
        const val HTTP_NOT_FOUND = 404
    }
}
