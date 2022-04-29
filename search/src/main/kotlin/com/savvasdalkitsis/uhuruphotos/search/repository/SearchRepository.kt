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
}