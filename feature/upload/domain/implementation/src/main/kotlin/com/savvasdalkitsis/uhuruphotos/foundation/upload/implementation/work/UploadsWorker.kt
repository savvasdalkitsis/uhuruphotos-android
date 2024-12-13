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
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@HiltWorker
class UploadsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val uploadUseCase: UploadUseCase,
    private val syncUseCase: SyncUseCase,
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
        val force = params.inputData.getBoolean(KEY_FORCE, false)
        val itemsFailed = mutableSetOf<UploadItem>()
        syncUseCase.observePendingItems().distinctUntilChanged().collectLatest { items ->
            for (item in items) {
                log { "Uploading item $item" }
                updateProgress(0)

                val result = uploadUseCase.upload(item = item, force = force) { current, total ->
                    updateProgress(current, total)
                }
                log { "Result of uploading item $item was $result" }
                if (result.isErr) {
                    log(result.error) { "Failed to upload item $item" }
                    itemsFailed += item
                }
            }
        }
        return when {
            itemsFailed.isEmpty() -> Result.success()
            else -> itemsFailed.failOrRetry()
        }
    }

    private fun Set<UploadItem>.failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        uploadUseCase.markAsNotUploading(*(this.map { it.id }.toLongArray()))
        Result.failure()
    }

    companion object {
        const val KEY_FORCE = "force"
        const val WORK_NAME = "uploadFiles"
        private const val NOTIFICATION_ID = 1292
    }
}