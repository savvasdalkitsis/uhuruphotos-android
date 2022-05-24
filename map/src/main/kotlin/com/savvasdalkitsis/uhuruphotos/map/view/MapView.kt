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
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
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