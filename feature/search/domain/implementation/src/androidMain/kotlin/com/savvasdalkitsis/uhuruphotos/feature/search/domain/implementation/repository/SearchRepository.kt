/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.service.SearchService
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.groupBy
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchService: SearchService,
    private val searchQueries: SearchQueries,
    private val remoteMediaItemSummaryQueries: RemoteMediaItemSummaryQueries,
    @PlainTextPreferences
    private val preferences: Preferences,
) {

    private val suggestions = "searchSuggestions"

    private val recentSearches = "recentSearches"

    fun observeSearchResults(query: String): Flow<Group<String, GetSearchResults>> =
        searchQueries.getSearchResults(query).asFlow().mapToList(Dispatchers.IO).groupBy(
            GetSearchResults::date)
            .distinctUntilChanged()

    suspend fun getSearchResults(query: String): Group<String, GetSearchResults> =
        searchQueries.getSearchResults(query).awaitList().groupBy(GetSearchResults::date).let(::Group)

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
    }.simple()

    fun getSearchSuggestions(): Flow<List<String>> = preferences.observeStringSet(suggestions, emptySet())
        .map { it.toList() }

    suspend fun refreshSearchSuggestions() = runCatchingWithLog {
        preferences.setStringSet(suggestions, searchService.getSearchSuggestions().results
            .map(String::trim)
            .toSet()
        )
    }.simple()

    fun getRecentSearches(): Flow<List<String>> = preferences.observeStringSet(recentSearches, emptySet())
        .map { it.toList() }

    fun addSearchToRecentSearches(query: String) {
        val recent = preferences.getStringSet(recentSearches, emptySet())
        preferences.setStringSet(recentSearches, recent + query)
    }

    fun removeFromRecentSearches(query: String) {
        val recent = preferences.getStringSet(recentSearches, emptySet())
        preferences.setStringSet(recentSearches, recent - query)
    }

    fun clearRecentSearchSuggestions() {
        preferences.setStringSet(recentSearches, emptySet())
    }
}
