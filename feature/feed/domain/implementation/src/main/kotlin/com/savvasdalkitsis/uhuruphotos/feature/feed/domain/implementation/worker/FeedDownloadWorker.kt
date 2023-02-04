package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast.CancelFeedDownloadWorkBroadcastReceiver
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class FeedDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val feedRepository: FeedRepository,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<CancelFeedDownloadWorkBroadcastReceiver>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = R.string.refreshing_feed,
    notificationId = NOTIFICATION_ID,
    cancelBroadcastReceiver = CancelFeedDownloadWorkBroadcastReceiver::class.java,
) {

    override suspend fun work() = withContext(Dispatchers.IO) {
        val shallow = params.inputData.getBoolean(KEY_SHALLOW, false)
        updateProgress(0)
        val result = feedRepository.refreshRemoteMediaCollections(shallow) { current, total ->
            updateProgress(current, total)
        }
        when {
            result.isSuccess -> Result.success()
            else -> Result.retry()
        }
    }

    companion object {
        // string value needs to remain as is for backwards compatibility
        const val WORK_NAME = "refreshAlbums"
        const val KEY_SHALLOW = "shallow"
        private const val NOTIFICATION_ID = 1273
    }
}