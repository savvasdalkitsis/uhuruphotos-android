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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.MyLocationPressed
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import io.mockk.mockk
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.createComposeRule
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalPermissionsApi::class)
@RunWith(RobolectricTestRunner::class)
class MyLocationClickTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val mapViewState = mockk<MapViewState>()
    private val actions = mutableListOf<HeatMapAction>()
    private var permissionRequests = 0

    @Test
    fun `asks for the location permission instead of locating without it`() {
        onMyLocationClicked(permission(granted = false), mapViewState, actions::add)

        assertEquals(1, permissionRequests)
        assertEquals(emptyList<HeatMapAction>(), actions)
    }

    @Test
    fun `locates the device once the permission is granted`() {
        onMyLocationClicked(permission(granted = true), mapViewState, actions::add)

        assertEquals(0, permissionRequests)
        assertEquals(listOf(MyLocationPressed(mapViewState)), actions)
    }

    @Test
    fun `the my location button locates the device while the map loads`() {
        showActions(HeatMapState(loading = true), permission(granted = true))

        composeRule.onNode(hasClickAction()).performClick()

        assertEquals(listOf(MyLocationPressed(mapViewState)), actions)
    }

    @Test
    fun `the my location button asks for the permission without it`() {
        showActions(HeatMapState(), permission(granted = false))

        composeRule.onNode(hasClickAction()).performClick()

        assertEquals(1, permissionRequests)
    }

    private fun showActions(state: HeatMapState, permission: PermissionState) {
        composeRule.setContent {
            ComposeResourcesContext()
            Row { Actions(state, actions::add, permission, mapViewState) }
        }
        composeRule.waitForIdle()
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    private fun permission(granted: Boolean) = object : PermissionState {
        override val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        override val status = if (granted) PermissionStatus.Granted else PermissionStatus.Denied(false)
        override fun launchPermissionRequest() {
            permissionRequests++
        }
    }
}
