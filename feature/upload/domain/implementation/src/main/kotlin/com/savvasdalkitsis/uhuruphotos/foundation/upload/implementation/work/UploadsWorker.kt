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
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.CurrentUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.math.toProgressPercent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.getString
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_uploading
import uhuruphotos_android.foundation.strings.api.generated.resources.x_of_x

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
        val itemsFailed = mutableSetOf<UploadItem>()
        syncUseCase.observePendingItems().distinctUntilChanged().collectLatest { items ->
            uploadUseCase.markAsUploading(items = items.toTypedArray())
            for ((index, item) in items.withIndex()) {
                val xOfX = getString(string.x_of_x, index + 1, items.size)
                log { "Uploading item $item ($xOfX)" }
                updateProgress(0, xOfX)
                item.updateCurrentUpload(0f)

                val result = uploadUseCase.upload(item) { current, total ->
                    updateProgress(current, total, " ($xOfX)")
                    item.updateCurrentUpload(current.toProgressPercent(total))
                }
                log { "Result of uploading item $item was $result" }
                uploadUseCase.setCurrentUpload(null)
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

    private fun UploadItem.updateCurrentUpload(percent: Float) {
        uploadUseCase.setCurrentUpload(CurrentUpload(this, percent))
    }

    private fun Set<UploadItem>.failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        uploadUseCase.markAsNotUploading(*(this.map { it.id }.toLongArray()))
        Result.failure()
    }

    companion object {
        const val WORK_NAME = "uploadFiles"
        private const val NOTIFICATION_ID = 1292
    }
}