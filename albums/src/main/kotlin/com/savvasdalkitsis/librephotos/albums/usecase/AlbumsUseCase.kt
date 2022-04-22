package com.savvasdalkitsis.librephotos.albums.usecase

import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.librephotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.librephotos.photos.service.model.isVideo
import com.savvasdalkitsis.librephotos.photos.model.Photo
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


class AlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val dateDisplayer: DateDisplayer,
    private val photosUseCase: PhotosUseCase,
    private val albumWorkScheduler: AlbumWorkScheduler,
) {

    fun getAlbums(): Flow<List<Album>> = albumsRepository.getAlbumsByDate()
        .map { albums ->
            albums.items.map { (id, photos) ->
                val albumDate = photos.firstOrNull()?.albumDate
                val albumLocation = photos.firstOrNull()?.albumLocation

                Album(
                    id = id,
                    photoCount = photos.size,
                    date = dateDisplayer.dateString(albumDate),
                    location = albumLocation ?: "",
                    photos = photos.mapNotNull { item ->
                        item.photoId?.let { id ->
                            Photo(
                                id = id,
                                thumbnailUrl = with(photosUseCase) {
                                    id.toThumbnailUrlFromId()
                                },
                                fullResUrl = with(photosUseCase) {
                                    id.toFullSizeUrlFromId(item.isVideo)
                                },
                                fallbackColor = item.dominantColor,
                                isFavourite = item.rating ?: 0 >= PhotosUseCase.FAVOURITES_RATING_THRESHOLD,
                                ratio = item.aspectRatio ?: 1.0f,
                                isVideo = item.isVideo,
                            )
                        }
                    }
                )
            }.filter { it.photos.isNotEmpty() }
        }
        .distinctUntilChanged()
        .onStart {
            CoroutineScope(Dispatchers.IO).launch {
                if (!albumsRepository.hasAlbums()) {
                    startRefreshAlbumsWork(shallow = false)
                }
            }
        }

    fun startRefreshAlbumsWork(shallow: Boolean) {
        albumWorkScheduler.scheduleAlbumsRefreshNow(shallow)
    }
}