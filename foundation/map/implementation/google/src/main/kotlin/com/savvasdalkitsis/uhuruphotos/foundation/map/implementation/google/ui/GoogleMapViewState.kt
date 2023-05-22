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
package com.savvasdalkitsis.uhuruphotos.foundation.map.implementation.google.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState

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
            val gradient = listOf(
                Color(0, 255, 255, 0),
                Color(0, 255, 255, 255),
                Color(0, 191, 255, 255),
                Color(0, 127, 255, 255),
                Color(0, 63, 255, 255),
                Color(0, 0, 255, 255),
                Color(0, 0, 223, 255),
                Color(0, 0, 191, 255),
                Color(0, 0, 159, 255),
                Color(0, 0, 127, 255),
                Color(63, 0, 91, 255),
                Color(127, 0, 63, 255),
                Color(191, 0, 31, 255),
                Color(255, 0, 0, 255)
            ).map { it.toArgb() }.toIntArray()
            TileOverlay(
                tileProvider = HeatmapTileProvider.Builder()
                    .data(pointsOnVisibleMap.map { it.toLatLng })
                    .gradient(Gradient(gradient, FloatArray(gradient.size) { it / (gradient.size - 1f)}))
                    .opacity(1.0)
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