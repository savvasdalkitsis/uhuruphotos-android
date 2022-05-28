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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.savvasdalkitsis.uhuruphotos.map.R
import com.savvasdalkitsis.uhuruphotos.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.map.model.MapOptions

@Composable
fun GoogleMapView(
    modifier: Modifier = Modifier,
    mapViewState: GoogleMapViewState,
    mapOptions: MapOptions.() -> MapOptions = { this },
    contentPadding: PaddingValues = PaddingValues(),
    onMapClick: (LatLon) -> Unit = {},
    content: @Composable (MapViewScope.() -> Unit)? = null,
) {
    val context = LocalContext.current
    val darkModeStyle = remember {
        MapStyleOptions(
            context.resources.getString(R.string.dark_mode)
        )
    }
    val options = mapOptions(
        MapOptions(
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
        )
    )
    val properties = MapProperties(
        isMyLocationEnabled = options.myLocationButtonEnabled
    ).let {
        when {
            MaterialTheme.colors.isLight -> it
            else -> it.copy(mapStyleOptions = darkModeStyle)
        }
    }
    GoogleMap(
        modifier = modifier,
        contentPadding = contentPadding,
        onMapClick = {
            onMapClick(LatLon(it.latitude, it.longitude))
        },
        cameraPositionState = mapViewState.cameraPositionState,
        properties = properties,
        uiSettings = options.let {
            MapUiSettings(
                compassEnabled = it.compassEnabled,
                indoorLevelPickerEnabled = it.indoorLevelPickerEnabled,
                mapToolbarEnabled = it.mapToolbarEnabled,
                myLocationButtonEnabled = it.myLocationButtonEnabled,
                rotationGesturesEnabled = it.rotationGesturesEnabled,
                scrollGesturesEnabled = it.scrollGesturesEnabled,
                scrollGesturesEnabledDuringRotateOrZoom = it.scrollGesturesEnabledDuringRotateOrZoom,
                tiltGesturesEnabled = it.tiltGesturesEnabled,
                zoomControlsEnabled = it.zoomControlsEnabled,
                zoomGesturesEnabled = it.zoomGesturesEnabled,
            )
        },
        content = {
            content?.invoke(GoogleMapsViewScope())
        },
    )
}