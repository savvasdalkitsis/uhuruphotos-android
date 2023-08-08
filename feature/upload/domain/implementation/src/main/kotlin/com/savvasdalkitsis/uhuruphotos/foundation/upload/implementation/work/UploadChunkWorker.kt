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
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadResult.ChunkUploaded
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadResult.Finished
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.work.UploadWorkScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadChunkWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val uploadUseCase: UploadUseCase,
    private val uploadWorkScheduler: UploadWorkScheduler,
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
        val offset = params.inputData.getLong(KEY_OFFSET, -1)
        val uploadId = params.inputData.getString(KEY_UPLOAD_ID)!!
        val item = UploadItem(
            id = params.inputData.getLong(KEY_ITEM_ID, -1),
            contentUri = params.inputData.getString(KEY_CONTENT_URI)!!
        )
        return when (val result = uploadUseCase.uploadChunk(item.contentUri, uploadId, offset)) {
            is Ok -> {
                when (val status = result.value) {
                    is ChunkUploaded -> {
                        uploadWorkScheduler.scheduleChunkUpload(
                            item = item,
                            offset = status.newOffset,
                            uploadId = status.uploadId,
                        )
                    }
                    is Finished -> uploadWorkScheduler.scheduleUploadCompletion(
                        item = item,
                        uploadId = status.uploadId,
                    )
                }
                Result.success()
            }
            else -> failOrRetry(item.id)
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
        const val KEY_CONTENT_URI = "contentUri"
        const val KEY_OFFSET = "offset"
        const val KEY_UPLOAD_ID = "uploadId"
        fun workName(id: Long, offset: Long) = "uploadChunk/$id/$offset"
        private const val NOTIFICATION_ID = 1285
    }
}