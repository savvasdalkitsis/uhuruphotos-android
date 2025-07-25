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
package com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.R.raw.map_style_dark
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode

@Composable
internal fun GoogleMapView(
    modifier: Modifier = Modifier,
    mapViewState: GoogleMapViewState,
    mapOptions: MapOptions.() -> MapOptions = { this },
    contentPadding: PaddingValues = PaddingValues(),
    onMapClick: () -> Unit = {},
    content: @Composable (MapViewScope.() -> Unit)? = null,
) {
    val context = LocalContext.current

    val options = mapOptions(MapOptions())
    val properties = MapProperties(
        isMyLocationEnabled = options.enableMyLocation
    )
    val dark = LocalThemeMode.current.isDark()

    GoogleMap(
        modifier = modifier,
        contentPadding = contentPadding,
        onMapClick = { onMapClick() },
        cameraPositionState = mapViewState.cameraPositionState,
        properties = properties,
        googleMapOptionsFactory = {
            GoogleMapOptions().apply {
                if (dark) {
                    MapStyleOptions.loadRawResourceStyle(context, map_style_dark)
                }
            }
        },
        uiSettings = options.let {
            MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = it.scrollGesturesEnabled,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = it.zoomControlsEnabled,
                zoomGesturesEnabled = it.zoomGesturesEnabled,
            )
        },
        content = {
            content?.invoke(mapViewState)
        },
    )
}