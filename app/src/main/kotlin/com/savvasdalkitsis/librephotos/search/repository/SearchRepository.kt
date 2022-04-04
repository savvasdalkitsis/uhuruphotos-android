package com.savvasdalkitsis.librephotos.search.repository

import com.savvasdalkitsis.librephotos.extensions.Group
import com.savvasdalkitsis.librephotos.extensions.crud
import com.savvasdalkitsis.librephotos.extensions.groupBy
import com.savvasdalkitsis.librephotos.photos.db.PhotoSummary
import com.savvasdalkitsis.librephotos.photos.db.PhotoSummaryQueries
import com.savvasdalkitsis.librephotos.search.GetSearchResults
import com.savvasdalkitsis.librephotos.search.SearchQueries
import com.savvasdalkitsis.librephotos.search.api.SearchService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchService: SearchService,
    private val searchQueries: SearchQueries,
    private val photoSummaryQueries: PhotoSummaryQueries,
) {

    suspend fun removeAllSearchResults() {
        crud { searchQueries.clearSearchResults() }
    }

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
}