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
package com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.toLatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState

internal class GoogleMapViewState(
    internal val cameraPositionState: CameraPositionState,
    override val initialPosition: LatLon,
    override val initialZoom: Float,
    private val tileOverlay: @Composable (TileProvider) -> Unit = { TileOverlay(tileProvider = it) },
) : MapViewState {

    private var bounds: LatLngBounds? = null

    @Composable
    override fun Marker(latLon: LatLon) {
        Marker(
            state = rememberUpdatedMarkerState(position = latLon.toLatLng),
        )
    }

    @Composable
    override fun HeatMap(
        allPoints: Collection<LatLon>,
        pointsOnVisibleMap: Collection<LatLon>,
    ) {
        if (pointsOnVisibleMap.isNotEmpty()) {
            tileOverlay(remember(pointsOnVisibleMap) { heatmapTileProvider(pointsOnVisibleMap) })
        }
    }

    @Composable
    override fun Composition(onStoppedMoving: (Viewport) -> Unit) {
        var startedMoving by remember { mutableStateOf(false) }
        if (cameraPositionState.isMoving) {
            startedMoving = true
        }
        if (startedMoving && !cameraPositionState.isMoving) {
            startedMoving = false
            with(cameraPositionState) {
                bounds = projection
                    ?.visibleRegion
                    ?.latLngBounds
                onStoppedMoving(
                    Viewport(
                        position.target.toLatLon,
                        position.zoom
                    )
                )
            }
        }
    }

    override fun contains(latLon: LatLon): Boolean =
        bounds?.contains(latLon.toLatLng) ?: false

    override suspend fun centerToLocation(latLon: LatLon) {
        onMain {
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLon.toLatLng))
        }
    }

    private val LatLng.toLatLon: LatLon
        get() = (latitude to longitude).toLatLon
}