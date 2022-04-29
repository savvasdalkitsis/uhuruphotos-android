package com.savvasdalkitsis.uhuruphotos.search.usecase

import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.photos.service.model.isVideo
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.search.repository.SearchRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.random.Random

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val dateDisplayer: DateDisplayer,
    private val photosUseCase: PhotosUseCase,
) {

    fun searchFor(query: String): Flow<List<Album>> =
        searchRepository.getSearchResults(query)
            .map { groups ->
                groups.items.map { (id, photos) ->
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
            }
            .map { albums ->
                albums
                    .map { album ->
                        val photos = album.photos.filter { photo ->
                            !photo.thumbnailUrl.isNullOrEmpty()
                        }
                        album.copy(photoCount = photos.size, photos = photos)
                    }
                    .filter { album ->
                        album.photoCount > 0
                    }
            }
            .distinctUntilChanged()
            .onStart {
                CoroutineScope(currentCoroutineContext() + Dispatchers.IO).launch {
                    searchRepository.refreshSearch(query)
                }
            }

    fun getSearchSuggestions(): Flow<String> = searchRepository.getSearchSuggestions()
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
        }.onStart {
            searchRepository.refreshSearchSuggestions()
        }
}