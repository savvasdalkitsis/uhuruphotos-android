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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.coroutines.binding.binding
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okio.IOException

@HiltWorker
class UploadPostCompletionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val mediaUseCase: RemoteMediaUseCase,
    private val feedUseCase: FeedUseCase,
    private val uploadUseCase: UploadUseCase,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<Nothing>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = string.finalizing_upload,
    notificationId = NOTIFICATION_ID,
    cancelBroadcastReceiver = null,
) {

    override suspend fun work(): Result {
        val hash = params.inputData.getString(KEY_HASH)!!
        val itemId = params.inputData.getLong(KEY_ITEM_ID, -1)
        val result = try {
            binding<Unit, Throwable> {
                val summary = mediaUseCase.getRemoteMediaItemSummary(hash)
                if (summary != null) {
                    feedUseCase.refreshCluster(summary.containerId).bind()
                } else {
                    throw IOException("Could not load remote media summary for $hash")
                }
            }
        } catch (e: Exception) {
            log(e)
            Err(e)
        }
        return when (result) {
            is Ok -> {
                uploadUseCase.markAsNotUploading(itemId)
                Result.success()
            }
            else -> failOrRetry(itemId)
        }
    }

    private fun failOrRetry(itemId: Long) = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        uploadUseCase.markAsNotUploading(itemId)
        Result.failure()
    }

    companion object {
        const val KEY_ITEM_ID = "itemId"
        const val KEY_HASH = "hash"
        fun workName(hash: String) = "postUploadSync/$hash"
        private const val NOTIFICATION_ID = 1286
    }
}