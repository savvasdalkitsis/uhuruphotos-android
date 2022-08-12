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
package com.savvasdalkitsis.uhuruphotos.implementation.search.repository

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.api.db.domain.model.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.api.db.media.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.api.db.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.api.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.implementation.search.service.SearchService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchService: SearchService,
    private val searchQueries: SearchQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    flowSharedPreferences: FlowSharedPreferences,
) {

    private val suggestions = flowSharedPreferences
        .getNullableStringSet("searchSuggestions", emptySet())

    private val recentSearches = flowSharedPreferences
        .getNullableStringSet("recentSearches", emptySet())

    fun observeSearchResults(query: String): Flow<Group<String, GetSearchResults>> =
        searchQueries.getSearchResults(query).asFlow().mapToList().groupBy(GetSearchResults::date)
            .distinctUntilChanged()

    suspend fun getSearchResults(query: String): Group<String, GetSearchResults> =
        searchQueries.getSearchResults(query).await().groupBy(GetSearchResults::date).let(::Group)

    suspend fun refreshSearch(query: String) = runCatchingWithLog {
        val results = searchService.search(query)
        for (searchResult in results.results) {
            for (photoSummary in searchResult.items) {
                searchQueries.addSearchResult(
                    id = null,
                    query = query,
                    date = searchResult.date,
                    location = searchResult.location,
                    photoId = photoSummary.id
                )
                remoteMediaItemSummaryQueries.insert(
                    DbRemoteMediaItemSummary(
                        id = photoSummary.id,
                        dominantColor = photoSummary.dominantColor,
                        url = photoSummary.url,
                        location = photoSummary.location,
                        date = photoSummary.date,
                        birthTime = photoSummary.birthTime,
                        aspectRatio = photoSummary.aspectRatio,
                        type = photoSummary.type,
                        videoLength = photoSummary.videoLength,
                        rating = photoSummary.rating,
                        containerId = query,
                    )
                )
            }
        }
    }

    fun getSearchSuggestions(): Flow<List<String>> = suggestions.asFlow()
        .map { it.orEmpty().toList() }

    suspend fun refreshSearchSuggestions() = runCatchingWithLog {
        suggestions.setAndCommit(
            searchService.getSearchSuggestions().results
                .map(String::trim)
                .toSet()
        )
    }

    fun getRecentSearches(): Flow<List<String>> = recentSearches.asFlow()
        .map { it.orEmpty().toList() }

    suspend fun addSearchToRecentSearches(query: String) {
        recentSearches.setAndCommit(recentSearches.get().orEmpty() + query)
    }

    suspend fun removeFromRecentSearches(query: String) {
        recentSearches.setAndCommit(recentSearches.get().orEmpty() - query)
    }

    suspend fun clearRecentSearchSuggestions() {
        recentSearches.setAndCommit(emptySet())
    }
}