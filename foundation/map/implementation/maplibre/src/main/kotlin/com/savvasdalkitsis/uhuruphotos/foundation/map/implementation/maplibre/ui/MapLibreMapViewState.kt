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
package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.maplibre.ui

import androidx.compose.runtime.Composable
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState

class MapLibreMapViewState(
    override val initialPosition: LatLon,
    override val initialZoom: Float,
) : MapViewState {

    var mapView: MapView? = null
    private var bounds: Pair<LatLng, LatLng>? = null
    private var onStoppedMoving: () -> Unit = {}

    override fun contains(latLon: LatLon): Boolean = bounds?.let { (ne, sw) ->
        latLon.lon < ne.longitude && latLon.lon > sw.longitude &&
                latLon.lat < ne.latitude && latLon.lat > sw.latitude
    } ?: false

    override suspend fun centerToLocation(latLon: LatLon) {
        mapView.withMap {
            cameraPosition = CameraPosition.Builder()
                .target(latLon.toLatLng)
                .zoom(cameraPosition.zoom)
                .build()
            finishedMoving()
        }
    }

    @Composable
    override fun Composition(onStoppedMoving: () -> Unit) {
        this.onStoppedMoving = onStoppedMoving
    }

    @Composable
    override fun Marker(latLon: LatLon) {
        mapView.addMarker(latLon)
    }

    @Composable
    override fun HeatMap(
        allPoints: Collection<LatLon>,
        pointsOnVisibleMap: Collection<LatLon>,
    ) {
        if (pointsOnVisibleMap.isNotEmpty()) {
            mapView.showHeatMap(pointsOnVisibleMap.toSet())
        }
    }

    fun finishedMoving() {
        mapView.withMap {
            bounds = with(projection.visibleRegion.latLngBounds) {
                northEast to southWest
            }
            onStoppedMoving()
        }
    }

}