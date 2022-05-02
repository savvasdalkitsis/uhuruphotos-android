package com.savvasdalkitsis.uhuruphotos.map.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import com.savvasdalkitsis.uhuruphotos.map.R

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    location: LatLng,
    zoom: Float = 10f,
    mapProperties: MapProperties = MapProperties(),
    contentPadding: PaddingValues = PaddingValues(),
    mapSettings: MapUiSettings.() -> MapUiSettings = { this },
    onMapClick: (LatLng) -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, zoom)
    }
    MapView(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        mapProperties = mapProperties,
        contentPadding = contentPadding,
        mapSettings = mapSettings,
        onMapClick = onMapClick,
        content = content,
    )
}

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    mapProperties: MapProperties = MapProperties(),
    mapSettings: MapUiSettings.() -> MapUiSettings = { this },
    contentPadding: PaddingValues = PaddingValues(),
    onMapClick: (LatLng) -> Unit = {},
    content: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val darkModeStyle = remember {
        MapStyleOptions(
            context.resources.getString(R.string.dark_mode)
        )
    }
    val properties = when {
        MaterialTheme.colors.isLight -> mapProperties
        else -> mapProperties.copy(
            mapStyleOptions = darkModeStyle
        )
    }
    GoogleMap(
        modifier = modifier,
        contentPadding = contentPadding,
        onMapClick = onMapClick,
        cameraPositionState = cameraPositionState,
        properties = properties,
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