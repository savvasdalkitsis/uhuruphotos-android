/*
Copyright 2022 Savvas Dalkitsis

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

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.rememberMapViewState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeatMap(
    state: HeatMapState,
    action: (HeatMapAction) -> Unit
) {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    if (state.initialViewport != null) {
        val mapViewState: MapViewState = rememberMapViewState(
            initialPosition = state.initialViewport.center,
            initialZoom = state.initialViewport.zoom,
        )
        when (LocalConfiguration.current.orientation) {
            ORIENTATION_LANDSCAPE -> SidePanelHeatMap(
                state,
                action,
                locationPermissionState,
                mapViewState
            )

            else -> BottomPanelHeatMap(state, action, locationPermissionState, mapViewState)
        }
    }

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted
            && !locationPermissionState.status.shouldShowRationale
        ) {
            locationPermissionState.launchPermissionRequest()
        }
    }
}