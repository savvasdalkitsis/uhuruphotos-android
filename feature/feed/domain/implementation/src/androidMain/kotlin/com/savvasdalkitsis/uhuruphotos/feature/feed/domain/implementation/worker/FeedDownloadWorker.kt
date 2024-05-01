/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast.CancelFeedDownloadWorkBroadcastReceiver
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.delay
import kotlin.LazyThreadSafetyMode.NONE

internal class FeedDownloadWorker(
    context: Context,
    private val params: WorkerParameters,
) : ForegroundNotificationWorker<CancelFeedDownloadWorkBroadcastReceiver>(
    context,
    params,
    notificationTitle = R.string.refreshing_feed,
    notificationId = NOTIFICATION_ID,
    foregroundInfoBuilder = NotificationModule.foregroundInfoBuilder,
    cancelBroadcastReceiver = CancelFeedDownloadWorkBroadcastReceiver::class.java,
) {

    private val feedRepository: FeedRepository by lazy(NONE) {
        FeedModule.feedRepository
    }

    override suspend fun work(): Result {
        val shallow = params.inputData.getBoolean(KEY_SHALLOW, false)
        updateProgress(0)
        val result = feedRepository.refreshRemoteMediaCollections(shallow) { current, total ->
            delay(300)
            updateProgress(current, total)
        }
        return when {
            result.isOk -> Result.success()
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
