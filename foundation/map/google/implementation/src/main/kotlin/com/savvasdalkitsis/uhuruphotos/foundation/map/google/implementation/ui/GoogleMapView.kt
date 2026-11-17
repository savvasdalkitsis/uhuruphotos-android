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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode

@Composable
internal fun GoogleMapView(
    modifier: Modifier,
    mapViewState: GoogleMapViewState,
    mapOptions: MapOptions.() -> MapOptions,
    contentPadding: PaddingValues,
    onMapClick: () -> Unit,
    content: @Composable (MapViewScope.() -> Unit)?,
) {
    val options = mapOptions(MapOptions())
    val properties = rememberGoogleMapProperties(
        enableMyLocation = options.enableMyLocation,
        isDark = LocalThemeMode.current.isDark(),
    )

    GoogleMap(
        modifier = modifier,
        contentPadding = contentPadding,
        onMapClick = { onMapClick() },
        cameraPositionState = mapViewState.cameraPositionState,
        properties = properties,
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