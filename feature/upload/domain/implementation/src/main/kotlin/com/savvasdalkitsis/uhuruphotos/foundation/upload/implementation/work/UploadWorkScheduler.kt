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

import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Completing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Initializing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Synchronising
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Uploading
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AutoBind
class UploadWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
) : UploadWorkScheduler {

    override fun scheduleUploadInitialization(item: UploadItem) = with(InitiateUploadWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(item.id),
            klass = InitiateUploadWorker::class,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            tags = setOf(UPLOAD_WORK_TAG, tagFor(item.id)),
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
            tags = setOf(UPLOAD_WORK_TAG, tagFor(item.id)),
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
            tags = setOf(UPLOAD_WORK_TAG, tagFor(item.id)),
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
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 10,
            backoffTimeUnit = TimeUnit.SECONDS,
            tags = setOf(UPLOAD_WORK_TAG, tagFor(itemId)),
        ) {
            putString(KEY_HASH, hash.hash)
            putLong(KEY_ITEM_ID, itemId)
        }
    }

    override fun monitorUploadJobs(): Flow<List<WorkInfo?>> =
        workerStatusUseCase.monitorUniqueJobsByTag(UPLOAD_WORK_TAG)

    private fun tagFor(itemId: Long) = "$UPLOAD_WORK_TAG_ITEM_ID_PREFIX::$itemId"

    override fun mediaItemIdFrom(workInfo: WorkInfo): Long? = workInfo.tags.firstOrNull {
        it.startsWith(UPLOAD_WORK_TAG_ITEM_ID_PREFIX)
    }?.split("::")?.get(1)?.toLongOrNull()

    override fun List<WorkInfo>.findType(jobType: UploadJobType): List<WorkInfo.State> = filter {
        it.tags.contains(jobType.workType.java.name)
    }.map { it.state }

    private val UploadJobType.workType get() = when(this) {
        Initializing -> InitiateUploadWorker::class
        Uploading -> UploadChunkWorker::class
        Completing -> UploadCompletionWorker::class
        Synchronising -> UploadPostCompletionWorker::class
    }

    companion object {
        const val UPLOAD_WORK_TAG = "uploadWorkTag"
        const val UPLOAD_WORK_TAG_ITEM_ID_PREFIX = "uploadWorkTagItemId"
    }
}