package com.savvasdalkitsis.librephotos.albums.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AlbumDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val albumsRepository: AlbumsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        albumsRepository.refreshAlbums()
        Result.success()
    }

    companion object {
        const val WORK_NAME = "refreshAlbums"
    }
}