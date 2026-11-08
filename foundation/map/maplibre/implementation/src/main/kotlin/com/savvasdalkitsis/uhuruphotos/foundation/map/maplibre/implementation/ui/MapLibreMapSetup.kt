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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import android.content.Context
import android.view.View
import androidx.compose.runtime.staticCompositionLocalOf
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.extensions.getActivity
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.utils.BitmapUtils
import java.net.URL
import kotlin.concurrent.thread

internal data class AttributionMargins(val start: Int, val top: Int, val end: Int, val bottom: Int)

internal data class MapLibreMapSetup(
    val state: MapLibreMapViewState,
    val options: MapOptions,
    val attributionMargins: AttributionMargins,
    val palette: BasemapPalette,
    val onMapClick: () -> Unit,
)

internal val LocalMapLibreViewFactory =
    staticCompositionLocalOf<(Context, MapLibreMapSetup) -> View> { ::createMapLibreMapView }

private fun createMapLibreMapView(context: Context, setup: MapLibreMapSetup): View =
    MapView(context).apply {
        bindTo(setup.state)
        context.getActivity()!!.lifecycle.addObserver(lifecycleObserver())
        getMapAsync { map -> map.setUp(this, setup) }
    }

private fun MapLibreMap.setUp(view: MapView, setup: MapLibreMapSetup) {
    applyOptions(setup.options, setup.attributionMargins)
    thread {
        val style = calmStyle(setup.palette)
        view.post { if (!view.isDestroyed) setStyle(style) { it.addMarkerImages(view.context) } }
    }
    cameraPosition = CameraPosition.Builder()
        .target(setup.state.initialPosition.toLatLng)
        .zoom(setup.state.initialZoom.toDouble())
        .build()
    addOnMapClickListener {
        setup.onMapClick()
        true
    }
}

private fun MapLibreMap.applyOptions(options: MapOptions, margins: AttributionMargins) =
    with(uiSettings) {
        isScrollGesturesEnabled = options.scrollGesturesEnabled
        isDoubleTapGesturesEnabled = options.zoomGesturesEnabled
        isRotateGesturesEnabled = false
        isQuickZoomGesturesEnabled = options.zoomGesturesEnabled
        isZoomGesturesEnabled = options.zoomGesturesEnabled
        isCompassEnabled = false
        setAttributionMargins(margins.start, margins.top, margins.end, margins.bottom)
    }

private fun calmStyle(palette: BasemapPalette): Style.Builder = runCatching {
    Style.Builder().fromJson(calmStyleJson(URL(PhotoPrismStyle.URI).readText(), palette))
}.getOrElse { Style.Builder().fromUri(PhotoPrismStyle.URI) }

private fun Style.addMarkerImages(context: Context) = Markers.entries.forEach { marker ->
    addImage(marker.id, BitmapUtils.getDrawableFromRes(context, marker.drawable, marker.tint)!!)
}
