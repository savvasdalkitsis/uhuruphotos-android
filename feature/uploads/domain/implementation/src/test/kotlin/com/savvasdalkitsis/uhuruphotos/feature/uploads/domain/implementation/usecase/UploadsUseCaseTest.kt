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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.implementation.usecase

import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.CANCELLED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.mockk
import org.junit.Test
import java.util.*

class UploadsUseCaseTest {

    private val fixedUUID = UUID.randomUUID()

    @Test
    fun `sorts uploads correctly`() {
        val underTest = UploadsUseCase(mockk(), mockk(), mockk())

        val result = with(underTest) {
            listOf(
                workInfo(ENQUEUED),
                workInfo(BLOCKED),
                workInfo(RUNNING),
                workInfo(CANCELLED),
                workInfo(FAILED),
                workInfo(SUCCEEDED),
                workInfo(RUNNING, progress = 10),
                workInfo(CANCELLED, progress = 10),
                workInfo(FAILED, progress = 10),
                workInfo(SUCCEEDED, progress = 10),
                workInfo(RUNNING, progress = 80),
                workInfo(CANCELLED, progress = 80),
                workInfo(FAILED, progress = 80),
                workInfo(SUCCEEDED, progress = 80),
            ).sort()
        }

        assertThat(result.toValues(), sameBeanAs(
            listOf(
                workInfo(RUNNING, progress = 80),
                workInfo(RUNNING, progress = 10),
                workInfo(RUNNING),
                workInfo(BLOCKED),
                workInfo(ENQUEUED),
                workInfo(FAILED, progress = 80),
                workInfo(CANCELLED, progress = 80),
                workInfo(FAILED, progress = 10),
                workInfo(CANCELLED, progress = 10),
                workInfo(FAILED),
                workInfo(CANCELLED),
                workInfo(SUCCEEDED, progress = 80),
                workInfo(SUCCEEDED, progress = 10),
                workInfo(SUCCEEDED),
            ).toValues()
        ))
    }

    private fun workInfo(state: State, progress: Int? = null): WorkInfo = WorkInfo(
        fixedUUID,
        state,
        Data.EMPTY,
        mutableListOf(),
        progress?.let {
            Data(mapOf(ForegroundNotificationWorker.Progress to it))
        } ?: Data.EMPTY,
        1,
        1
    )

    private data class Values(
        val state: State,
        val progress: Int? = null
    )

    private fun List<WorkInfo>.toValues() = map {
        Values(it.state, ForegroundNotificationWorker.getProgressOrNullOf(it))
    }
}
