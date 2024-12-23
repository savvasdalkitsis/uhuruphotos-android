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
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
) : UploadWorkScheduler {

    override fun scheduleUploads(
        networkType: NetworkType,
        requiresCharging: Boolean,
    ) = with(UploadsWorker) {
        log { "Will schedule upload of local items" }
        workScheduleUseCase.scheduleNow(
            workName = WORK_NAME,
            klass = UploadsWorker::class,
            existingWorkPolicy = REPLACE,
            networkRequirement = networkType,
            requiresCharging = requiresCharging,
        )
    }

    override fun schedulePostUploadProcessing(
        hash: MediaItemHashModel,
        itemId: Long,
    ) = with(UploadPostCompletionWorker) {
        log { "Will schedule post completion of $hash" }
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
}