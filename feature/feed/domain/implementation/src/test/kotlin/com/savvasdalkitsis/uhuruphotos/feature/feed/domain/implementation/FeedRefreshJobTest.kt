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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation

import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.FeedDownloadWorker
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class FeedRefreshJobTest {

    private val periodicId = UUID.randomUUID()
    private val refreshNowId = UUID.randomUUID()
    private val workerStatusUseCase = mockk<WorkerStatusUseCase>()
    private val underTest = FeedWorkScheduler(mockk(), workerStatusUseCase)

    private fun work(periodic: WorkInfo.State?, refreshNow: WorkInfo.State?) {
        every { workerStatusUseCase.monitorUniqueJob(FeedDownloadWorker.WORK_NAME) } returns
            flowOf(periodic?.let { WorkInfo(periodicId, it, emptySet()) })
        every { workerStatusUseCase.monitorUniqueJob(FeedDownloadWorker.REFRESH_NOW_WORK_NAME) } returns
            flowOf(refreshNow?.let { WorkInfo(refreshNowId, it, emptySet()) })
    }

    @Test
    fun `reports the periodic refresh while it runs`() = runTest {
        work(periodic = RUNNING, refreshNow = ENQUEUED)

        assertEquals(RUNNING, underTest.observeFeedRefreshJob().first()?.status)
    }

    @Test
    fun `reports a requested refresh while the periodic one waits`() = runTest {
        work(periodic = ENQUEUED, refreshNow = RUNNING)

        assertEquals(RUNNING, underTest.observeFeedRefreshJobStatus().first())
    }

    @Test
    fun `reports the periodic refresh once a requested refresh finished`() = runTest {
        work(periodic = ENQUEUED, refreshNow = SUCCEEDED)

        assertEquals(ENQUEUED, underTest.observeFeedRefreshJobStatus().first())
    }

    @Test
    fun `reports a finished refresh when no periodic refresh exists`() = runTest {
        work(periodic = null, refreshNow = SUCCEEDED)

        assertEquals(SUCCEEDED, underTest.observeFeedRefreshJobStatus().first())
    }

    @Test
    fun `reports nothing when no refresh exists`() = runTest {
        work(periodic = null, refreshNow = null)

        assertEquals(null, underTest.observeFeedRefreshJob().first())
    }
}
