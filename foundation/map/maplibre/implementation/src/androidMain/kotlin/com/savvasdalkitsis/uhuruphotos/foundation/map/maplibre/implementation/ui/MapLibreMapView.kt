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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.utils.BitmapUtils
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.extensions.getActivity
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.api.MapLibreApi

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    mapViewState: MapLibreMapViewState,
    contentPadding: PaddingValues = PaddingValues(),
    mapOptions: MapOptions.() -> MapOptions = { this },
    onMapClick: () -> Unit = {},
    content: @Composable (MapViewScope.() -> Unit)?,
) {

    val colors = MaterialTheme.colors
    val layoutDirection = LocalLayoutDirection.current

    val start = with(contentPadding) {
        calculateStartPadding(layoutDirection).toPixel()
    }
    val end = with(contentPadding) {
        calculateEndPadding(layoutDirection).toPixel()
    }
    val top = with(contentPadding) {
        calculateTopPadding().toPixel()
    }
    val bottom = with(contentPadding) {
        calculateBottomPadding().toPixel()
    }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                bindTo(mapViewState)
                context.getActivity()!!.lifecycle.addObserver(this.lifecycleObserver())
                getMapAsync { map ->
                    with(mapOptions(MapOptions())) {
                        with(map.uiSettings) {
                            isScrollGesturesEnabled = scrollGesturesEnabled
                            isDoubleTapGesturesEnabled = zoomGesturesEnabled
                            isRotateGesturesEnabled = false
                            isQuickZoomGesturesEnabled = zoomGesturesEnabled
                            isZoomGesturesEnabled = zoomGesturesEnabled
                            isCompassEnabled = false
                            setAttributionMargins(start, top, end, bottom)
                        }
                    }
                    map.setStyle(
                        when {
                            colors.isLight -> "https://api.maptiler.com/maps/basic-v2/style.json?key="
                            else -> "https://api.maptiler.com/maps/basic-v2-dark/style.json?key="
                        } + MapLibreApi.getApiKey(context)
                    )
                    map.getStyle { style ->
                        Markers.entries.forEach { marker ->
                            style.addImage(
                                marker.id,
                                BitmapUtils.getDrawableFromRes(context, marker.drawable, marker.tint)!!,
                            )
                        }
                    }
                    map.cameraPosition = CameraPosition.Builder()
                        .target(mapViewState.initialPosition.toLatLng)
                        .zoom(mapViewState.initialZoom.toDouble())
                        .build()
                    map.addOnMapClickListener {
                        onMapClick()
                        true
                    }
                }
            }
        }
    )
    content?.invoke(mapViewState)
}

@Composable
private fun Dp.toPixel() = with(LocalDensity.current) {
    toPx()
}.toInt()