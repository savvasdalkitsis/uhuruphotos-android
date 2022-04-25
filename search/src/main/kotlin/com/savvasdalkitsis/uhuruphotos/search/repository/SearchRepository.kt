package com.savvasdalkitsis.uhuruphotos.search.repository

import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.groupBy
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummary
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.db.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.search.service.SearchService
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