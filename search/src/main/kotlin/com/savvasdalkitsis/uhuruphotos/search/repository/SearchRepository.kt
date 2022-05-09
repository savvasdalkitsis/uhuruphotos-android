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
package com.savvasdalkitsis.uhuruphotos.search.repository

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.db.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.groupBy
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.search.service.SearchService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchService: SearchService,
    private val searchQueries: SearchQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
    flowSharedPreferences: FlowSharedPreferences,
) {

    private val suggestions = flowSharedPreferences
        .getNullableStringSet("searchSuggestions", emptySet())

    private val recentSearches = flowSharedPreferences
        .getNullableStringSet("recentSearches", emptySet())

    fun getSearchResults(query: String): Flow<Group<String, GetSearchResults>> =
        searchQueries.getSearchResults(query).asFlow().mapToList().groupBy(GetSearchResults::date)
            .distinctUntilChanged()

    suspend fun refreshSearch(query: String) {
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
                photoSummaryQueries.insert(
                    PhotoSummary(
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

    suspend fun refreshSearchSuggestions() {
        try {
            suggestions.setAndCommit(
                searchService.getSearchSuggestions().results
                    .map(String::trim)
                    .toSet()
            )
        } catch (e: IOException) {
            log(e)
        }
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