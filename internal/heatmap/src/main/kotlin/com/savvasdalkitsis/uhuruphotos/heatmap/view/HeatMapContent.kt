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
package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction
import com.savvasdalkitsis.uhuruphotos.map.Locations
import com.savvasdalkitsis.uhuruphotos.map.view.MapView
import com.savvasdalkitsis.uhuruphotos.ui.insets.insetsTop
import kotlinx.coroutines.withContext

@Composable
fun HeatMapContent(
    modifier: Modifier = Modifier,
    action: (HeatMapAction) -> Unit,
    locationPermissionState: PermissionState,
    state: HeatMapState
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(Locations.TRAFALGAR_SQUARE, 2f)
    }
    var startedMoving by remember { mutableStateOf(false) }
    if (cameraPositionState.isMoving) {
        startedMoving = true
    }
    val scope = rememberCoroutineScope()
    if (startedMoving && !cameraPositionState.isMoving) {
        @Suppress("UNUSED_VALUE")
        startedMoving = false
        action(HeatMapAction.CameraViewPortChanged {  latLng ->
            withContext(scope.coroutineContext) {
                cameraPositionState.projection?.visibleRegion?.latLngBounds?.contains(latLng)
                    ?: false
            }
        })
    }

    val showLocationButton = locationPermissionState.status.isGranted

    MapView(
        modifier = modifier,
        contentPadding = PaddingValues(top = insetsTop() + 56.dp),
        cameraPositionState = cameraPositionState,
        mapProperties = MapProperties(
            isMyLocationEnabled = showLocationButton,
        ),
        mapSettings = {
            copy(
                scrollGesturesEnabled = true,
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                myLocationButtonEnabled = showLocationButton,
            )
        }
    ) {
        if (state.pointsToDisplay.isNotEmpty()) {
            TileOverlay(
                tileProvider = HeatmapTileProvider.Builder()
                    .data(state.pointsToDisplay)
                    .build()
            )
        }
    }
}