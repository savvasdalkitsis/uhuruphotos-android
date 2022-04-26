package com.savvasdalkitsis.uhuruphotos.albums.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.icons.R
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.notification.NotificationChannels
import com.savvasdalkitsis.uhuruphotos.notification.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.notification.initializer.NotificationInitializer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AlbumDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val albumsRepository: AlbumsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val shallow = params.inputData.getBoolean(KEY_SHALLOW, false)
            albumsRepository.refreshAlbums(shallow)
            Result.success()
        } catch (e: Exception) {
            log(e)
            Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = Builder(applicationContext, JOBS_CHANNEL_ID)
            .setContentTitle("Refreshing albums")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification
            )
        }
    }
    companion object {
        const val WORK_NAME = "refreshAlbums"
        const val KEY_SHALLOW = "shallow"
        private const val NOTIFICATION_ID = 1273
    }
}