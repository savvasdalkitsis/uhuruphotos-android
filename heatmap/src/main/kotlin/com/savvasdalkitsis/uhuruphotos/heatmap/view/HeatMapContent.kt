package com.savvasdalkitsis.uhuruphotos.heatmap.view

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
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