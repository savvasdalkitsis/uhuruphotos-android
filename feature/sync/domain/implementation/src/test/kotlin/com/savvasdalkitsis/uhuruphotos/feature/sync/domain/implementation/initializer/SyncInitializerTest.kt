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

    @Test
    fun `schedules uploads once items are queued`() = runTest {
        val syncUseCase: SyncUseCase = mockk {
            every { observePendingItems() } returns flowOf(
                emptyList(),
                listOf(UploadItem(1, "content://1")),
                listOf(UploadItem(1, "content://1"), UploadItem(2, "content://2")),
                emptyList(),
                listOf(UploadItem(3, "content://3")),
            )
        }

        val requirements = SyncInitializer(syncUseCase, mockk(), settingsUseCase)
            .uploadRequirementsWhilePending()
            .toList()

        assertEquals(listOf(NetworkType.UNMETERED to false, NetworkType.UNMETERED to false), requirements)
    }

    @Test
    fun `does not schedule uploads without queued items`() = runTest {
        val syncUseCase: SyncUseCase = mockk {
            every { observePendingItems() } returns flowOf(emptyList())
        }

        val requirements = SyncInitializer(syncUseCase, mockk(), settingsUseCase)
            .uploadRequirementsWhilePending()
            .toList()

        assertEquals(emptyList<Pair<NetworkType, Boolean>>(), requirements)
    }
}
