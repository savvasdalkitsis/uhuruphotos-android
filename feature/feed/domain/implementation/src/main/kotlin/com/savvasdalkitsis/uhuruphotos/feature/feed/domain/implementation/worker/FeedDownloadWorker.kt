package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
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
    private val foregroundInfoBuilder: com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val shallow = params.inputData.getBoolean(KEY_SHALLOW, false)
        val result = feedRepository.refreshRemoteMediaCollections(shallow) { progress ->
            setProgress(workDataOf(Progress to progress))
            createForegroundInfo(progress)
        }
        when {
            result.isSuccess -> Result.success()
            else -> Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = createForegroundInfo(null)

    private fun createForegroundInfo(progress: Int?) = foregroundInfoBuilder.build(
        applicationContext,
        R.string.refreshing_feed,
        NOTIFICATION_ID,
        NotificationChannels.JOBS_CHANNEL_ID,
        progress,
    )
    companion object {
        const val Progress = "Progress"
        // string value needs to remain as is for backwards compatibility
        const val WORK_NAME = "refreshAlbums"
        const val KEY_SHALLOW = "shallow"
        private const val NOTIFICATION_ID = 1273
    }
}