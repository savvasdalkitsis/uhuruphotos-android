/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui

import android.content.Context
import android.view.View
import androidx.compose.runtime.staticCompositionLocalOf
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer.MapBoxSdk

internal class MapBoxMapSetup(
    val sdk: MapBoxSdk,
    val state: MapBoxMapViewState,
    val options: MapOptions,
    val isDark: Boolean,
    val onMapClick: () -> Unit,
)

internal val LocalMapBoxViewFactory =
    staticCompositionLocalOf<(Context, MapBoxMapSetup) -> View> { ::createMapBoxMapView }

internal fun MapBoxSdk.newMapView(
    context: Context,
    create: (Context) -> MapView = ::MapView,
): MapView {
    ensureLoaded()
    return create(context)
}

internal fun mapBoxStyle(isDark: Boolean): String = when {
    isDark -> Style.DARK
    else -> Style.MAPBOX_STREETS
}

private fun createMapBoxMapView(context: Context, setup: MapBoxMapSetup): View =
    setup.sdk.newMapView(context).apply {
        applyOptions(setup.options)
        mapboxMap.loadStyle(mapBoxStyle(setup.isDark)) { showInitialPosition(setup.state) }
        scalebar.enabled = false
        compass.enabled = false
        bindTo(setup.state)
        mapboxMap.addOnMapClickListener {
            setup.onMapClick()
            true
        }
    }

private fun MapView.applyOptions(options: MapOptions) {
    gestures.doubleTouchToZoomOutEnabled = options.zoomGesturesEnabled
    gestures.pinchToZoomEnabled = options.zoomGesturesEnabled
    gestures.doubleTapToZoomInEnabled = options.zoomGesturesEnabled
    gestures.scrollEnabled = options.scrollGesturesEnabled
    gestures.rotateEnabled = false
}

private fun MapView.showInitialPosition(state: MapBoxMapViewState) {
    mapboxMap.setCamera(
        CameraOptions.Builder()
            .center(state.initialPosition.toPoint)
            .zoom(state.initialZoom.toDouble())
            .build()
    )
    location.updateSettings {
        enabled = true
        pulsingEnabled = true
    }
}
