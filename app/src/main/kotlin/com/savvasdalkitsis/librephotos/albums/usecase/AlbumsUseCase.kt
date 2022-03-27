package com.savvasdalkitsis.librephotos.albums.usecase

import android.text.format.DateUtils
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.librephotos.photos.model.Photo
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DateFormat
import javax.inject.Inject


class AlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val serverUseCase: ServerUseCase,
    private val dateFormat: DateFormat,
) {

    fun getAlbums(
        refresh: Boolean = true,
        onDoneRefreshing: suspend () -> Unit = {},
    ): Flow<List<Album>> {
        if (refresh) {
            refreshAlbums(onDoneRefreshing)
        }
        val now = System.currentTimeMillis()
        return albumsRepository.getAlbumsByDate()
            .map { albums ->
                albums.items.map { (_, photos) ->
                    val serverUrl = serverUseCase.getServerUrl()
                    val albumDate = photos.firstOrNull()?.albumDate
                    val albumLocation = photos.firstOrNull()?.albumLocation

                    Album(
                        photoCount = photos.size,
                        date = dateString(albumDate, now),
                        location = albumLocation ?: "",
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

    private fun dateString(albumDate: String?, now: Long): String = when (albumDate) {
        null -> ""
        else -> when (val millis = dateFormat.parse(albumDate)) {
            null -> ""
            else -> DateUtils.getRelativeTimeSpanString(
                millis.time,
                now,
                DateUtils.DAY_IN_MILLIS
            ).toString()
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