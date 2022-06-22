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

import androidx.compose.runtime.Composable
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.toCameraOptions
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapViewState

internal class MapBoxMapViewState(
    override val initialPosition: LatLon,
    override val initialZoom: Float,
) : MapViewState {

    var mapView: MapView? = null
    private val map get() = mapView?.getMapboxMap()
    private var bounds: Pair<Point, Point>? = null
    private var onStoppedMoving: () -> Unit = {}

    @Composable
    override fun Marker(latLon: LatLon) {
        mapView?.addMarker(latLon)
    }

    @Composable
    override fun HeatMap(
        allPoints: Collection<LatLon>,
        pointsOnVisibleMap: Collection<LatLon>,
    ) {
        if (pointsOnVisibleMap.isNotEmpty()) {
            mapView?.showHeatMap(pointsOnVisibleMap.toSet())
        }
    }

    @Composable
    override fun Composition(onStoppedMoving: () -> Unit) {
        this.onStoppedMoving = onStoppedMoving
    }

    override fun contains(latLon: LatLon): Boolean = bounds?.let { (ne, sw) ->
        latLon.lon < ne.longitude() && latLon.lon > sw.longitude() &&
                latLon.lat < ne.latitude() && latLon.lat > sw.latitude()
    } ?: false

    override suspend fun centerToLocation(latLon: LatLon) {
        map?.setCamera(CameraOptions.Builder()
            .center(latLon.toPoint)
            .build())
        finishedMoving()
    }

    fun finishedMoving() {
        map?.let {
            val coords = it.coordinateBoundsForCamera(it.cameraState.toCameraOptions())
            bounds = coords.northeast to coords.southwest
        }
        onStoppedMoving()
    }
}