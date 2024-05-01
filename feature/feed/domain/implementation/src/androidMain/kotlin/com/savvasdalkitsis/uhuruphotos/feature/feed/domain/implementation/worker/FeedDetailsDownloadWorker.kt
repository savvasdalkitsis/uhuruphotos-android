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
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast.CancelFeedDetailsDownloadWorkBroadcastReceiver
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResult.CHANGED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlin.LazyThreadSafetyMode.NONE

internal class FeedDetailsDownloadWorker(
    context: Context,
    private val params: WorkerParameters,
) : ForegroundNotificationWorker<CancelFeedDetailsDownloadWorkBroadcastReceiver>(
    context,
    params,
    notificationTitle = R.string.refreshing_feed_details,
    notificationId = NOTIFICATION_ID,
    foregroundInfoBuilder = NotificationModule.foregroundInfoBuilder,
    cancelBroadcastReceiver = CancelFeedDetailsDownloadWorkBroadcastReceiver::class.java,
) {

    private val feedUseCase: FeedUseCase by lazy(NONE) {
        FeedModule.feedUseCase
    }
    private val mediaUseCase: MediaUseCase by lazy(NONE) {
        CommonMediaModule.mediaUseCase
    }

    override suspend fun work(): Result {
        updateProgress(0)
        val items = feedUseCase.observeFeed(FeedFetchType.ALL, loadSmallInitialChunk = false).first()
            .flatMap { it.mediaItems }.mapNotNull { it.id.findRemote }
        val total = items.size
        val result = coroutineBinding {
            items.forEachIndexed { current, item ->
                val retrieval = mediaUseCase.refreshDetailsNowIfMissing(item).bind()
                if (retrieval == CHANGED) {
                    // don't overwhelm the server and our CPU
                    delay(300)
                }
                updateProgress(current, total)
            }
        }
        return when {
            result.isOk -> Result.success()
            else -> failOrRetry()
        }
    }

    private fun failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        Result.failure()
    }

    companion object {
        const val WORK_NAME = "refreshFeedDetails"
        private const val NOTIFICATION_ID = 1282
    }
}
