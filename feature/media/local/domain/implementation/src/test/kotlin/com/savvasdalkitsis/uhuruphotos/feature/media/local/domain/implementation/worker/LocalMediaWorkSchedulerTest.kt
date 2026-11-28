/*
Copyright 2026 Savvas Dalkitsis

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
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class LocalMediaWorkSchedulerTest {

    private val workScheduleUseCase = mockk<WorkScheduleUseCase>(relaxed = true)
    private val underTest = LocalMediaWorkScheduler(workScheduleUseCase)

    private fun verifyScheduledOffline(policy: ExistingWorkPolicy) {
        verify {
            workScheduleUseCase.scheduleNow(
                workName = LocalMediaSyncWorker.WORK_NAME,
                klass = LocalMediaSyncWorker::class,
                existingWorkPolicy = policy,
                networkRequirement = NetworkType.NOT_REQUIRED,
            )
        }
    }

    @Test
    fun `scans local media while offline when no scan is running`() {
        underTest.scheduleLocalMediaSyncNowIfNotRunning()

        verifyScheduledOffline(ExistingWorkPolicy.KEEP)
    }

    @Test
    fun `scans local media while offline`() {
        underTest.scheduleLocalMediaSyncNow()

        verifyScheduledOffline(ExistingWorkPolicy.REPLACE)
    }
}
