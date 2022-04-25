package com.savvasdalkitsis.uhuruphotos.map.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    location: LatLng,
    zoom: Float = 10f,
    mapSettings: MapUiSettings.() -> MapUiSettings = { this },
    onMapClick: (LatLng) -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, zoom)
    }
    GoogleMap(
        modifier = modifier,
        onMapClick = onMapClick,
        cameraPositionState = cameraPositionState,
        uiSettings = mapSettings(MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false,
        )),
        content = content,
    )
}