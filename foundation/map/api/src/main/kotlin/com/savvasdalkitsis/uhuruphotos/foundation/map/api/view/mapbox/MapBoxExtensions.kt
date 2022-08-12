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
package com.savvasdalkitsis.uhuruphotos.foundation.map.api.view.mapbox

import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.android.gestures.StandardScaleGestureDetector
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.extension.style.layers.addLayerBelow
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.OnScaleListener
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.gestures.addOnScaleListener
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

internal fun MapView.addMarker(marker: LatLon) {
    annotations.createCircleAnnotationManager().create(
        CircleAnnotationOptions()
            .withPoint(marker.toPoint)
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")
    )
}

internal fun MapView.showHeatMap(points: Set<LatLon>) {
    getMapboxMap().getStyle {
        with(it) {
            removeStyleLayer("heatMap")
            removeStyleLayer("heatMapCircles")
            removeStyleSource("heatMapPoints")
            addSource(createHeatMapSource("heatMapPoints", points))
            addLayerAbove(
                createHeatmapLayer(
                    layerId = "heatMap",
                    sourceId = "heatMapPoints",
                ),
                above = "waterway-label",
            )
            addLayerBelow(
                createHeatMapCircles(
                    layerId = "heatMapCircles",
                    sourceId = "heatMapPoints",
                ),
                below = "heatMap",
            )
        }
    }
}


internal fun MapView.bindTo(mapViewState: MapBoxMapViewState) {
    mapViewState.mapView = this
    getMapboxMap().apply {
        addOnMoveListener(object : OnMoveListener {
            override fun onMove(detector: MoveGestureDetector) = false

            override fun onMoveBegin(detector: MoveGestureDetector) {}

            override fun onMoveEnd(detector: MoveGestureDetector) {
                mapViewState.finishedMoving()
            }
        })
        addOnScaleListener(object: OnScaleListener {
            override fun onScale(detector: StandardScaleGestureDetector) {}

            override fun onScaleBegin(detector: StandardScaleGestureDetector) {}

            override fun onScaleEnd(detector: StandardScaleGestureDetector) {
                mapViewState.finishedMoving()
            }

        })
    }
}