package com.savvasdalkitsis.librephotos.albums.usecase

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.librephotos.albums.worker.AlbumDownloadWorker
import com.savvasdalkitsis.librephotos.date.DateDisplayer
import com.savvasdalkitsis.librephotos.photos.api.model.isVideo
import com.savvasdalkitsis.librephotos.photos.model.Photo
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


class AlbumsUseCase @Inject constructor(
    private val albumsRepository: AlbumsRepository,
    private val dateDisplayer: DateDisplayer,
    private val photosUseCase: PhotosUseCase,
    private val workManager: WorkManager,
) {

    fun getAlbums(
        refresh: Boolean = true,
    ): Flow<List<Album>> = albumsRepository.getAlbumsByDate()
        .map { albums ->
            albums.items.map { (_, photos) ->
                val albumDate = photos.firstOrNull()?.albumDate
                val albumLocation = photos.firstOrNull()?.albumLocation

                Album(
                    photoCount = photos.size,
                    date = dateDisplayer.dateString(albumDate),
                    location = albumLocation ?: "",
                    photos = photos.map { item ->
                        Photo(
                            url = with(photosUseCase) {
                                item.photoId.toThumbnailUrlFromId(item.isVideo)
                            },
                            fallbackColor = item.dominantColor,
                            ratio = item.aspectRatio ?: 1.0f,
                            isVideo = item.isVideo,
                        )
                    }
                )
            }
        }.onStart {
            CoroutineScope(Dispatchers.IO).launch {
                if (refresh || !albumsRepository.hasAlbums()) {
                    workManager.enqueueUniqueWork(
                        AlbumDownloadWorker.WORK_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequestBuilder<AlbumDownloadWorker>()
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .build()
                    )
                }
            }
        }
}