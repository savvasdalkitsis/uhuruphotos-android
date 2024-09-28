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
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast.CancelFeedDetailsDownloadWorkBroadcastReceiver
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResultModel.CHANGED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@HiltWorker
internal class FeedDetailsDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val feedUseCase: FeedUseCase,
    private val mediaUseCase: MediaUseCase,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<CancelFeedDetailsDownloadWorkBroadcastReceiver>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = R.string.refreshing_feed_details,
    notificationId = NOTIFICATION_ID,
    cancelBroadcastReceiver = CancelFeedDetailsDownloadWorkBroadcastReceiver::class.java,
) {

    override suspend fun work(): Result {
        updateProgress(0)
        val items = feedUseCase.observeFeed(FeedFetchTypeModel.ALL, loadSmallInitialChunk = false).first()
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
