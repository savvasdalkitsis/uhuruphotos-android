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
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val uploadUseCase: UploadUseCase,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<Nothing>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = string.media_sync_status_uploading,
    notificationId = NOTIFICATION_ID,
    cancelBroadcastReceiver = null,
) {

    override suspend fun work(): Result {
        val item = UploadItem(
            id = params.inputData.getLong(KEY_ITEM_ID, -1),
            contentUri = params.inputData.getString(KEY_CONTENT_URI)!!
        )

        val result = uploadUseCase.upload(item) { current, total ->
            updateProgress(current, total)
        }

        return when (result) {
            is Ok -> Result.success()
            is Err -> result.failOrRetry(item.id)
        }
    }

    private fun Err<Throwable>.failOrRetry(itemId: Long) = if (params.runAttemptCount < 4) {
        log(error) { "Failed to upload item $itemId, retrying" }
        Result.retry()
    } else {
        log(error) { "Failed to upload item $itemId, not retrying" }
        uploadUseCase.markAsNotUploading(itemId)
        Result.failure()
    }

    companion object {
        const val KEY_ITEM_ID = "itemId"
        const val KEY_CONTENT_URI = "contentUri"
        fun workName(id: Long) = "uploadFile/$id"
        private const val NOTIFICATION_ID = 1288
    }
}