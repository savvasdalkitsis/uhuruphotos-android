package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

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