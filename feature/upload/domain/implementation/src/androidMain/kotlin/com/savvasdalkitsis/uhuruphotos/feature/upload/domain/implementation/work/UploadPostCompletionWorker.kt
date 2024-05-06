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
package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.work

import android.content.Context
import androidx.work.WorkerParameters
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.onFailure
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus.Found
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaItemSummaryStatus.Processing
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.module.RemoteMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.module.UploadModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.ServiceLocator
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import java.util.concurrent.TimeUnit
import kotlin.LazyThreadSafetyMode.NONE

class UploadPostCompletionWorker(
    context: Context,
    private val params: WorkerParameters,
) : ForegroundNotificationWorker<Nothing>(
    context,
    params,
    notificationTitle = strings.finalizing_upload,
    notificationId = NOTIFICATION_ID,
    foregroundInfoBuilder = NotificationModule.foregroundInfoBuilder,
    cancelBroadcastReceiver = null,
) {

    private val mediaUseCase: RemoteMediaUseCase by lazy(NONE) {
        RemoteMediaModule.remoteMediaUseCase
    }
    private val feedUseCase: FeedUseCase by lazy(NONE) {
        ServiceLocator.get()
    }
    private val uploadUseCase: UploadUseCase by lazy(NONE) {
        UploadModule.uploadUseCase
    }

    private val itemId get() = params.inputData.getLong(KEY_ITEM_ID, -1)

    override suspend fun work(): Result = coroutineBinding {
        val hash = params.inputData.getString(KEY_HASH)!!
        when (val status = mediaUseCase.getRemoteMediaItemSummary(hash).bind()) {
            is Found -> {
                feedUseCase.refreshCluster(status.summary.containerId).bind()
                uploadUseCase.markAsNotProcessing(itemId)
                Result.success()
            }
            is Processing -> {
                uploadUseCase.saveLastResponseForProcessingItem(itemId, status.response.toString())
                failOrRetry(itemId)
            }
        }
    }.onFailure {
        uploadUseCase.saveErrorForProcessingItem(itemId, it)
    }.getOr(Result.retry())

    private fun failOrRetry(itemId: Long) = if (params.runAttemptCount < SCHEDULE_MAX_ATTEMPTS) {
        Result.retry()
    } else {
        uploadUseCase.markAsNotProcessing(itemId)
        Result.failure()
    }

    companion object {
        const val KEY_ITEM_ID = "itemId"
        const val KEY_HASH = "hash"
        const val SCHEDULE_DELAY = 10L
        val SCHEDULE_UNIT = TimeUnit.SECONDS
        private const val SCHEDULE_MAX_ATTEMPTS = 60480 // amount of 10 second intervals in a week
        fun workName(hash: String) = "postUploadSync/$hash"
        private const val NOTIFICATION_ID = 1287
    }
}