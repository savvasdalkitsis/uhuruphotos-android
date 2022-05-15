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
package com.savvasdalkitsis.uhuruphotos.search.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.db.search.GetSearchResults
import com.savvasdalkitsis.uhuruphotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.Group
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.service.model.isVideo
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.search.api.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.search.repository.SearchRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    override fun searchFor(query: String): Flow<Result<List<Album>, Throwable>> =
        searchRepository.observeSearchResults(query)
            .map { groups ->
                groups.mapToAlbums()
            }
            .distinctUntilChanged()
            .safelyOnStart {
                searchRepository.refreshSearch(query)
            }

    private suspend fun Group<String, GetSearchResults>.mapToAlbums() = items
        .map { (id, photos) ->
            val albumLocation = photos.firstOrNull()?.location
            val albumDate = photos.firstOrNull()?.date
            Album(
                id = id,
                photoCount = photos.size,
                date = dateDisplayer.dateString(albumDate),
                location = albumLocation,
                photos = photos.mapNotNull { photo ->
                    photo.summaryId?.let { id ->
                        Photo(
                            id = id,
                            thumbnailUrl = with(photosUseCase) {
                                photo.summaryId.toThumbnailUrlFromId()
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
            album.copy(photoCount = photos.size, photos = photos)
        }
        .filter { album ->
            album.photoCount > 0
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
