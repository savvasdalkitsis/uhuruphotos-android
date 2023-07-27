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
package com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSource
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.implementation.repository.SearchRepository
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.onStartWithResult
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.mapNotNullValues
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import kotlin.random.Random

@AutoBind
class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val dateDisplayer: DateDisplayer,
    private val mediaUseCase: MediaUseCase,
) : SearchUseCase {

    override suspend fun searchResultsFor(query: String): List<MediaCollection> = with(mediaUseCase) {
        searchRepository.getSearchResults(query)
            .mapNotNullValues { it.toMediaCollectionSource() }
            .toMediaCollection()
    }

    override fun searchFor(query: String): Flow<Result<List<MediaCollection>, Throwable>> =
        searchRepository.observeSearchResults(query)
            .distinctUntilChanged()
            .map { groups ->
                with(mediaUseCase) {
                    groups.mapNotNullValues { it.toMediaCollectionSource() }
                        .toMediaCollection()
                }
            }
            .distinctUntilChanged()
            .onStartWithResult {
                searchRepository.refreshSearch(query)
            }

    private fun GetSearchResults.toMediaCollectionSource() = summaryId?.let { id ->
        MediaCollectionSource(
            id = id,
            location = location,
            date = dateDisplayer.dateString(date),
            mediaItemId = id,
            dominantColor = dominantColor,
            aspectRatio = aspectRatio,
            isVideo = isVideo,
            rating = null,
            lat = null,
            lon = null,
        )
    }

    override fun getRandomSearchSuggestion(): Flow<String> = getSearchSuggestions()
        .flatMapLatest { suggestions ->
            val r = Random(System.currentTimeMillis())
            flow {
                if (suggestions.size > 1) {
                    while (currentCoroutineContext().isActive) {
                        emit(suggestions[r.nextInt(suggestions.size - 1)])
                        delay(3000)
                    }
                } else if (suggestions.size == 1) {
                    emit(suggestions[0])
                }
            }
        }
        .safelyOnStartIgnoring {
            searchRepository.refreshSearchSuggestions()
        }

    override fun getSearchSuggestions(): Flow<List<String>> = searchRepository.getSearchSuggestions()

    override fun getRecentTextSearches(): Flow<List<String>> = searchRepository.getRecentSearches()

    override suspend fun addSearchToRecentSearches(query: String) {
        searchRepository.addSearchToRecentSearches(query)
    }

    override suspend fun removeFromRecentSearches(query: String) {
        searchRepository.removeFromRecentSearches(query)
    }

    override suspend fun clearRecentSearchSuggestions() {
        searchRepository.clearRecentSearchSuggestions()
    }
}
