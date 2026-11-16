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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel.RequiresPermissionsModel
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test

class FeedStateTest {

    @Test
    fun `has no headers when nothing asks for one`() {
        assertEquals(false, FeedState().hasHeaders)
    }

    @Test
    fun `has headers when any header is asked for`() {
        val withHeaders = listOf(
            FeedState(memories = persistentListOf(MemoryCelState(yearsAgo = 1, cels = persistentListOf()))),
            FeedState(showRequestPermissionForLocalMediaAccess = RequiresPermissionsModel(emptyList())),
            FeedState(localMediaSyncRunning = true),
            FeedState(showRequestForCloudSync = true),
            FeedState(showLoginBanner = true),
            FeedState(showBatteryOptimizationBanner = true),
        )

        assertEquals(List(withHeaders.size) { true }, withHeaders.map { it.hasHeaders })
    }
}
