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
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.toMediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.UploadService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import okhttp3.MultipartBody.Part.Companion.createFormData

@HiltWorker
class UploadCompletionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val userUseCase: UserUseCase,
    private val uploadUseCase: UploadUseCase,
    private val uploadService: UploadService,
    private val uploadWorkScheduler: UploadWorkScheduler,
    private val localMediaUseCase: LocalMediaUseCase,
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
        val uploadId = params.inputData.getString(KEY_UPLOAD_ID)!!
        val itemId = params.inputData.getLong(KEY_ITEM_ID, -1)
        val result = try {
            binding<Unit, Throwable> {
                val userId = userUseCase.getUserOrRefresh().bind().id
                val mediaItem = localMediaUseCase.getLocalMediaItem(itemId)
                    ?: throw IllegalArgumentException("Could not find associated local media with id: $itemId")
                val md5 = mediaItem.md5
                val filename = mediaItem.displayName
                    ?: throw IllegalArgumentException("No name associated with file $itemId")
                uploadService.completeUpload(
                    uploadId = createFormData("upload_id", uploadId),
                    user = createFormData("user", userId.toString()),
                    md5 = createFormData("md5", md5.value),
                    filename = createFormData("filename", filename),
                )
                delay(1000)
                uploadWorkScheduler.schedulePostUploadSync(md5.toMediaItemHash(userId), itemId)
            }
        } catch (e: Exception) {
            log(e)
            Err(e)
        }
        return when (result) {
            is Ok -> {
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
        const val KEY_UPLOAD_ID = "uploadId"
        const val KEY_ITEM_ID = "itemId"
        fun workName(uploadId: String) = "finalizingUpload/$uploadId"
        private const val NOTIFICATION_ID = 1286
    }
}