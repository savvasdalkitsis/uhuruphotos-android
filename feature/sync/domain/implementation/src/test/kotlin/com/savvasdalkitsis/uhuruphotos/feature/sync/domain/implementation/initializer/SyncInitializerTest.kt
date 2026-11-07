/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.initializer

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncInitializerTest {

    private val settingsUseCase: SettingsUseCase = mockk {
        every { observeCloudSyncNetworkRequirements() } returns flowOf(NetworkType.UNMETERED)
        every { observeCloudSyncRequiresCharging() } returns flowOf(false)
    }
    private val replace = SyncInitializer.UploadSchedule(NetworkType.UNMETERED, false, keepExisting = false)
    private val keep = replace.copy(keepExisting = true)

    private fun schedules(vararg pending: List<UploadItem>) = SyncInitializer(
        mockk {
            every { observePendingItems() } returns flow {
                pending.forEach {
                    emit(it)
                    delay(1)
                }
            }
        },
        mockk(),
        settingsUseCase,
    ).uploadSchedules()

    private fun items(vararg ids: Long) = ids.map { UploadItem(it, "content://$it") }

    @Test
    fun `schedules uploads once items are queued`() = runTest {
        assertEquals(
            listOf(replace, replace),
            schedules(items(), items(1), items(1), items(), items(3)).toList(),
        )
    }

    @Test
    fun `keeps the running upload when more items get queued`() = runTest {
        assertEquals(
            listOf(replace, keep),
            schedules(items(1), items(1, 2), items(2), items(2)).toList(),
        )
    }

    @Test
    fun `does not schedule uploads without queued items`() = runTest {
        assertEquals(
            emptyList<SyncInitializer.UploadSchedule>(),
            schedules(items()).toList(),
        )
    }

    @Test
    fun `replaces the upload when its requirements change`() = runTest {
        every { settingsUseCase.observeCloudSyncNetworkRequirements() } returns flow {
            emit(NetworkType.UNMETERED)
            delay(1)
            emit(NetworkType.CONNECTED)
        }

        assertEquals(
            listOf(replace, replace.copy(networkType = NetworkType.CONNECTED)),
            schedules(items(1)).toList(),
        )
    }
}
