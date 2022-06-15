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

import androidx.compose.runtime.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.savvasdalkitsis.uhuruphotos.api.launchers.onMain
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapViewState

internal class GoogleMapViewState(
    internal val cameraPositionState: CameraPositionState,
    override val initialPosition: LatLon,
    override val initialZoom: Float,
) : MapViewState {

    private var bounds: LatLngBounds? = null

    @Composable
    override fun Marker(latLon: LatLon) {
        Marker(
            state = MarkerState(position = latLon.toLatLng),
        )
    }

    @Composable
    override fun HeatMap(
        allPoints: Collection<LatLon>,
        pointsOnVisibleMap: Collection<LatLon>,
    ) {
        if (pointsOnVisibleMap.isNotEmpty()) {
            TileOverlay(
                tileProvider = HeatmapTileProvider.Builder()
                    .data(pointsOnVisibleMap.map { it.toLatLng })
                    .build()
            )
        }
    }

    @Composable
    override fun Composition(onStoppedMoving: () -> Unit) {
        var startedMoving by remember { mutableStateOf(false) }
        if (cameraPositionState.isMoving) {
            startedMoving = true
        }
        if (startedMoving && !cameraPositionState.isMoving) {
            @Suppress("UNUSED_VALUE")
            startedMoving = false
            bounds = cameraPositionState
                .projection
                ?.visibleRegion
                ?.latLngBounds
            onStoppedMoving()
        }
    }

    override fun contains(latLon: LatLon): Boolean =
        bounds?.contains(latLon.toLatLng) ?: false

    override suspend fun centerToLocation(latLon: LatLon) {
        onMain {
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLon.toLatLng))
        }
    }
}