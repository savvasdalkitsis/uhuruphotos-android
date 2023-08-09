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

import androidx.work.ExistingWorkPolicy
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import se.ansman.dagger.auto.AutoBind
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AutoBind
class UploadWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase
) : UploadWorkScheduler {

    override fun scheduleUploadInitialization(item: UploadItem) = with(InitiateUploadWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(item.id),
            klass = InitiateUploadWorker::class,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
        ) {
            putLong(KEY_ID, item.id)
            putString(KEY_CONTENT_URI, item.contentUri)
        }
    }

    override fun scheduleChunkUpload(
        item: UploadItem,
        offset: Long,
        uploadId: String
    ) = with(UploadChunkWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(item.id, offset),
            klass = UploadChunkWorker::class,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
        ) {
            putLong(KEY_ITEM_ID, item.id)
            putString(KEY_CONTENT_URI, item.contentUri)
            putString(KEY_UPLOAD_ID, uploadId)
            putLong(KEY_OFFSET, offset)
        }
    }

    override fun scheduleUploadCompletion(
        item: UploadItem,
        uploadId: String,
    ) = with(UploadCompletionWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(uploadId),
            klass = UploadCompletionWorker::class,
        ) {
            putString(KEY_UPLOAD_ID, uploadId)
            putLong(KEY_ITEM_ID, item.id)
        }
    }

    override fun schedulePostUploadSync(
        hash: MediaItemHash,
        itemId: Long,
    ) = with(UploadPostCompletionWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(hash.hash),
            klass = UploadPostCompletionWorker::class,
            backoffDelay = 10,
            backoffTimeUnit = TimeUnit.SECONDS,
        ) {
            putString(KEY_HASH, hash.hash)
            putLong(KEY_ITEM_ID, itemId)
        }
    }
}