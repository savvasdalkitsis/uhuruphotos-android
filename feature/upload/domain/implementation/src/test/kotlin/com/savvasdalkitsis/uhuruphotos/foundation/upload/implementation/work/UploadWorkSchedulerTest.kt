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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.work

import androidx.work.ExistingWorkPolicy
import androidx.work.ExistingWorkPolicy.KEEP
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.UNMETERED
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class UploadWorkSchedulerTest {

    private val workScheduleUseCase: WorkScheduleUseCase = mockk(relaxed = true)
    private val underTest = UploadWorkScheduler(workScheduleUseCase)

    private fun scheduledPolicy(keepExisting: Boolean): ExistingWorkPolicy {
        val policy = slot<ExistingWorkPolicy>()

        underTest.scheduleUploads(UNMETERED, requiresCharging = false, keepExisting = keepExisting)

        verify {
            workScheduleUseCase.scheduleNow(
                UploadsWorker.WORK_NAME, UploadsWorker::class, capture(policy),
                any(), any(), any(), UNMETERED, false, any(), any(),
            )
        }
        return policy.captured
    }

    @Test
    fun `keeps the running upload when more items get queued`() {
        assertEquals(KEEP, scheduledPolicy(keepExisting = true))
    }

    @Test
    fun `replaces the upload when it has to start over`() {
        assertEquals(REPLACE, scheduledPolicy(keepExisting = false))
    }
}
