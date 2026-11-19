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
package com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class WorkerStatusUseCaseTest {

    private val updates = (1..500).map { listOf(WorkInfo(UUID.randomUUID(), WorkInfo.State.RUNNING, emptySet())) }
    private val work = MutableLiveData<List<WorkInfo>>()
    private val workManager = mockk<WorkManager> {
        every { getWorkInfosForUniqueWorkLiveData("sync") } returns work
    }
    private val underTest = WorkerStatusUseCase(workManager)

    @Test
    fun `reports work updates in the order they happen`() {
        val received = CoroutineScope(Dispatchers.Default).async {
            underTest.monitorUniqueJob("sync").take(updates.size).toList()
        }
        idleUntil { work.hasObservers() }

        updates.forEach { work.value = it }
        idleUntil { received.isCompleted }

        assertEquals(updates.map { it.single().id }, received.getCompleted().map { it?.id })
    }

    private fun idleUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 10000
        while (!condition() && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
        }
    }
}
