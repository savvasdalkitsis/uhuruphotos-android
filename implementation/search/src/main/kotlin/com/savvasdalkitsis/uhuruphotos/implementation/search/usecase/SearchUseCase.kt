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
package com.savvasdalkitsis.uhuruphotos.implementation.search.usecase

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.db.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.api.group.model.Group
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.search.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.search.repository.SearchRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.random.Random

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val dateDisplayer: DateDisplayer,
    private val photosUseCase: PhotosUseCase,
) : SearchUseCase {

    override suspend fun searchResultsFor(query: String): List<Album> =
        searchRepository.getSearchResults(query)
            .mapToAlbums()

    override fun searchFor(query: String): Flow<Result<List<Album>>> =
        searchRepository.observeSearchResults(query)
            .map { groups ->
                groups.mapToAlbums()
            }
            .distinctUntilChanged()
            .safelyOnStart {
                searchRepository.refreshSearch(query)
            }

    private fun Group<String, GetSearchResults>.mapToAlbums() = items
        .map { (id, photos) ->
            val albumLocation = photos.firstOrNull()?.location
            val albumDate = photos.firstOrNull()?.date
            Album(
                id = id,
                date = dateDisplayer.dateString(albumDate),
                location = albumLocation,
                photos = photos.mapNotNull { photo ->
                    photo.summaryId?.let { id ->
                        Photo(
                            id = id,
                            thumbnailUrl = with(photosUseCase) {
                                photo.summaryId.toThumbnailUrlFromIdNullable()
                            },
                            fallbackColor = photo.dominantColor,
                            ratio = photo.aspectRatio ?: 1f,
                            isVideo = photo.isVideo,
                        )
                    }
                }
            )
        }
        .map { album ->
            val photos = album.photos.filter { photo ->
                !photo.thumbnailUrl.isNullOrEmpty()
            }
            album.copy(photos = photos)
        }
        .filter { album ->
            album.photos.isNotEmpty()
        }

    override fun getRandomSearchSuggestion(): Flow<String> = getSearchSuggestions()
        .flatMapLatest { suggestions ->
            val r = Random(System.currentTimeMillis())
            flow {
                if (suggestions.isNotEmpty()) {
                    while (currentCoroutineContext().isActive) {
                        emit(suggestions[r.nextInt(suggestions.size - 1)])
                        delay(3000)
                    }
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
