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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.CameraViewPortChanged
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapView
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets.insetsTop

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SharedTransitionScope.HeatMapContent(
    modifier: Modifier = Modifier,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState,
    state: HeatMapState,
    mapViewState: MapViewState
) {
    mapViewState.Composition(onStoppedMoving = { viewport ->
        action(CameraViewPortChanged(viewport, newBoundsChecker = { latLng ->
            mapViewState.contains(latLng)
        }))
    })

    val locationPermissionGranted = locationPermissionState.status.isGranted

    MapView(
        modifier = modifier,
        mapViewState = mapViewState,
        mapOptions = {
            copy(
                scrollGesturesEnabled = true,
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                enableMyLocation = locationPermissionGranted,
            )
        },
        contentPadding = PaddingValues(top = insetsTop() + 56.dp, bottom = 24.dp)
    ) {
        HeatMap(state.allPoints, state.pointsOnVisibleMap)
    }
}