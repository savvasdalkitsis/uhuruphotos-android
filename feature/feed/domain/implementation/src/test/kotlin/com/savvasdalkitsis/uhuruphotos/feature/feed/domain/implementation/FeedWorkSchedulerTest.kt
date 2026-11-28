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

import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.CANCELLED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.FeedDownloadWorker
import com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase.WorkScheduleUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.implementation.usecase.WorkerStatusUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class FeedWorkSchedulerTest {

    private lateinit var workManager: WorkManager
    private lateinit var underTest: FeedWorkScheduler

    @Before
    fun setUp() {
        val context = RuntimeEnvironment.getApplication()
        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            Configuration.Builder()
                .setExecutor(SynchronousExecutor())
                .setTaskExecutor(SynchronousExecutor())
                .build(),
        )
        workManager = WorkManager.getInstance(context)
        underTest = FeedWorkScheduler(WorkScheduleUseCase(workManager), WorkerStatusUseCase(workManager))
    }

    private fun feedWork(): List<WorkInfo> =
        workManager.getWorkInfosByTag(FeedDownloadWorker::class.java.name).get()

    private fun oneTimeStates() = feedWork().filter { it.periodicityInfo == null }.map { it.state }

    private fun periodicStates() = feedWork().filter { it.periodicityInfo != null }.map { it.state }

    @Test
    fun `refreshing now while the periodic refresh waits queues a refresh`() {
        underTest.scheduleFeedRefreshPeriodic()

        underTest.scheduleFeedRefreshNow()

        assertEquals(listOf(ENQUEUED), oneTimeStates())
        assertEquals(listOf(ENQUEUED), periodicStates())
    }

    @Test
    fun `cancelling the feed refresh keeps a periodic refresh scheduled`() {
        underTest.scheduleFeedRefreshPeriodic()
        underTest.scheduleFeedRefreshNow()

        underTest.cancelFeedSync()

        assertEquals(listOf(CANCELLED), oneTimeStates())
        assertEquals(listOf(ENQUEUED), periodicStates().filter { !it.isFinished })
    }
}
