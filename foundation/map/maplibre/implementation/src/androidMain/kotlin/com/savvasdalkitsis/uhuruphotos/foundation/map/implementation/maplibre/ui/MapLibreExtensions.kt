/*
Copyright 2023 Savvas Dalkitsis

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

import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon


internal fun MapView.bindTo(mapViewState: MapLibreMapViewState) {
    mapViewState.mapView = this
    getMapAsync {
        addOnCameraDidChangeListener {
            mapViewState.finishedMoving()
        }
    }
}

internal fun MapView.lifecycleObserver() = object : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        onCreate(Bundle())
    }

    override fun onStart(owner: LifecycleOwner) {
        onStart()
    }

    override fun onResume(owner: LifecycleOwner) {
        onResume()
    }

    override fun onPause(owner: LifecycleOwner) {
        onPause()
    }

    override fun onStop(owner: LifecycleOwner) {
        onStop()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
    }
}

internal fun MapView?.withMap(action: MapboxMap.(MapView) -> Unit) {
    this?.let { view -> view.getMapAsync { action(it, view) } }
}

internal fun MapView?.withStyle(action: MapboxMap.(MapView, Style) -> Unit) = withMap { view ->
    getStyle {
        action(this, view, it)
    }
}

internal fun MapView?.addMarker(latLon: LatLon) {
    withStyle { view, style ->
        SymbolManager(view, this@withStyle, style).create(
            SymbolOptions()
                .withLatLng(latLon.toLatLng)
                .withIconImage(Markers.Pin.id)
                .withIconSize(1.3f)
        )
    }
}

internal fun MapView?.showHeatMap(points: Set<LatLon>) {
    withStyle { _, style ->
        with(style) {
            removeLayer("heatMap")
            removeLayer("heatMapCircles")
            removeSource("heatMapPoints")
            addSource(createHeatMapSource("heatMapPoints", points))
            addLayer(
                createHeatmapLayer(
                    layerId = "heatMap",
                    sourceId = "heatMapPoints",
                ),
            )
            addLayerBelow(
                createHeatMapCircles(
                    layerId = "heatMapCircles",
                    sourceId = "heatMapPoints",
                ),
                "heatMap",
            )
        }
    }
}
