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
package com.savvasdalkitsis.uhuruphotos.api.map.view.mapbox

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapViewScope

@Composable
internal fun MapBoxMapView(
    modifier: Modifier,
    mapOptions: MapOptions.() -> MapOptions = { this },
    onMapClick: () -> Unit = {},
    mapViewState: MapBoxMapViewState,
    content: @Composable (MapViewScope.() -> Unit)?,
) {
    val style = when {
        MaterialTheme.colors.isLight -> Style.MAPBOX_STREETS
        else -> Style.DARK
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                getMapboxMap().apply {
                    with(mapOptions(MapOptions())) {
                        gestures.doubleTouchToZoomOutEnabled = zoomGesturesEnabled
                        gestures.pinchToZoomEnabled = zoomGesturesEnabled
                        gestures.doubleTapToZoomInEnabled = zoomGesturesEnabled
                        gestures.scrollEnabled = scrollGesturesEnabled
                        gestures.rotateEnabled = false
                    }
                    loadStyleUri(style)
                    scalebar.enabled = false
                    compass.enabled = false
                    addOnStyleLoadedListener {
                        setCamera(
                            CameraOptions.Builder()
                                .center(mapViewState.initialPosition.toPoint)
                                .zoom(mapViewState.initialZoom.toDouble())
                                .build()
                        )
                        location.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }
                    }
                    bindTo(mapViewState)
                    addOnMapClickListener {
                        onMapClick()
                        true
                    }
                }
            }
        }
    ) { mapView ->
        mapViewState.markers.value.forEach { marker ->
            mapView.addMarker(marker)
        }
        val heatMap = mapViewState.heatMapPoints.value
        if (heatMap.isNotEmpty()) {
            mapView.showHeatMap(heatMap)
        }
    }
    content?.invoke(MapViewScope(mapViewState))
}