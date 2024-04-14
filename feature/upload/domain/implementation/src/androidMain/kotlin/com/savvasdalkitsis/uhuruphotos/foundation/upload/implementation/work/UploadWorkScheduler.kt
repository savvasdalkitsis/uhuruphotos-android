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
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
) : UploadWorkScheduler {

    override fun scheduleUpload(
        force: Boolean,
        item: UploadItem,
        networkType: NetworkType,
        requiresCharging: Boolean,
    ) = with(UploadWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(item.id),
            klass = UploadWorker::class,
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            networkRequirement = networkType,
            requiresCharging = requiresCharging,
            tags = setOf(UPLOAD_WORK_TAG, tagFor(item.id)),
        ) {
            putLong(KEY_ITEM_ID, item.id)
            putString(KEY_CONTENT_URI, item.contentUri)
            putBoolean(KEY_FORCE, force)
        }
    }

    override fun schedulePostUploadProcessing(
        hash: MediaItemHash,
        itemId: Long,
    ) = with(UploadPostCompletionWorker) {
        workScheduleUseCase.scheduleNow(
            workName = workName(hash.hash),
            klass = UploadPostCompletionWorker::class,
            backoffPolicy = BackoffPolicy.LINEAR,
            backoffDelay = SCHEDULE_DELAY,
            backoffTimeUnit = SCHEDULE_UNIT,
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

    companion object {
        const val UPLOAD_WORK_TAG = "uploadWorkTag"
        const val UPLOAD_WORK_TAG_ITEM_ID_PREFIX = "uploadWorkTagItemId"
    }
}