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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker

import androidx.work.ExistingWorkPolicy
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class LocalMediaWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
): LocalMediaWorkScheduler {

    override fun scheduleLocalMediaSyncNowIfNotRunning() {
        workScheduleUseCase.scheduleNow(
            workName = LocalMediaSyncWorker.WORK_NAME,
            klass = LocalMediaSyncWorker::class,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
        )
    }

    override fun cancelLocalMediaSync() {
        workScheduleUseCase.cancelUniqueWork(LocalMediaSyncWorker.WORK_NAME)
    }
}
