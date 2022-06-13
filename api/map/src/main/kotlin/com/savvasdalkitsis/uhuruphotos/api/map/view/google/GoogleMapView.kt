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
package com.savvasdalkitsis.uhuruphotos.api.map.view.google

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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.savvasdalkitsis.uhuruphotos.api.map.R
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapViewScope

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
    val darkModeStyle = remember {
        MapStyleOptions(
            context.resources.getString(R.string.dark_mode)
        )
    }
    val options = mapOptions(MapOptions())
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
        onMapClick = { onMapClick() },
        cameraPositionState = mapViewState.cameraPositionState,
        properties = properties,
        uiSettings = options.let {
            MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = it.myLocationButtonEnabled,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = it.scrollGesturesEnabled,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = it.zoomControlsEnabled,
                zoomGesturesEnabled = it.zoomGesturesEnabled,
            )
        },
        content = {
            content?.invoke(MapViewScope(mapViewState))
            mapViewState.markers.value.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.toLatLng),
                )
            }
            val points = mapViewState.heatMapPoints.value
            if (points.isNotEmpty()) {
                TileOverlay(
                    tileProvider = HeatmapTileProvider.Builder()
                        .data(points.map { it.toLatLng })
                        .build()
                )
            }
        },
    )
}