package com.savvasdalkitsis.librephotos.albums.usecase

import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.librephotos.photos.model.Photo
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val serverUseCase: ServerUseCase,
) {

    fun getAlbums(
        refresh: Boolean = true,
        onDoneRefreshing: suspend () -> Unit = {},
    ): Flow<List<Album>> {
        if (refresh) {
            refreshAlbums(onDoneRefreshing)
        }
        return albumsRepository.getAlbumsByDate()
            .map { albums ->
                albums.items.map { (_, photos) ->
                    val serverUrl = serverUseCase.getServerUrl()
                    Album(
                        photoCount = photos.size,
                        photos = photos.map { item ->
                            val url = item.bigThumbnailUrl?.removeSuffix(".webp")
                            Photo(
                                url = url?.let { serverUrl + it },
                                fallbackColor = item.dominantColor,
                                ratio = item.aspectRatio ?: 1.0f,
                            )
                        }
                    )
                }
            }
    }

    private fun refreshAlbums(
        onDoneRefreshing: suspend () -> Unit = {},
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            albumsRepository.refreshAlbums()
            onDoneRefreshing()
        }
    }
}