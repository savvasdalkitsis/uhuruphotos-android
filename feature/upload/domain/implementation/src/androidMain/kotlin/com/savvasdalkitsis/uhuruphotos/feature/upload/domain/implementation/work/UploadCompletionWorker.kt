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
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.module.UploadModule
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.module.NotificationModule
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import kotlin.LazyThreadSafetyMode.NONE

@Deprecated("This class is no longer used. Left for backwards compatibility")
class UploadCompletionWorker(
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

    private val uploadUseCase: UploadUseCase by lazy(NONE) {
        UploadModule.uploadUseCase
    }
    private val uploadRepository: UploadRepository by lazy(NONE) {
        UploadModule.uploadRepository
    }
    private val localMediaUseCase: LocalMediaUseCase by lazy(NONE) {
        LocalMediaModule.localMediaUseCase
    }

    override suspend fun work(): Result {
        val itemId = params.inputData.getLong(KEY_ITEM_ID, -1)
        uploadRepository.setCompleted(itemId)

        return try {
            val mediaItem = localMediaUseCase.getLocalMediaItem(itemId)
                ?: throw IllegalArgumentException("Could not find associated local media with id: $itemId")
            uploadUseCase.scheduleUpload(items = arrayOf(UploadItem(itemId, mediaItem.contentUri)))
            Result.success()
        } catch (e: Exception) {
            log(e) { "Failed to schedule upload for item $itemId" }
            failOrRetry(itemId)
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
        private const val NOTIFICATION_ID = 1286
    }
}