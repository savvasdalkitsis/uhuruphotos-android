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
package com.savvasdalkitsis.uhuruphotos.api.map.view

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.rememberCameraPositionState
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapProvider.MapBox
import com.savvasdalkitsis.uhuruphotos.api.map.view.google.GoogleMapViewState
import com.savvasdalkitsis.uhuruphotos.api.map.view.mapbox.MapBoxMapViewState

interface MapViewState : MapViewScope {
    val initialPosition: LatLon
    val initialZoom: Float
    fun contains(latLon: LatLon): Boolean
    suspend fun centerToLocation(latLon: LatLon)
    @Composable
    fun Composition(onStoppedMoving: () -> Unit)
}

@Composable
fun rememberMapViewState(
    initialPosition: LatLon,
    initialZoom: Float,
): MapViewState = when (LocalMapProvider.current) {
    Google -> rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition.toLatLng, initialZoom)
    }.let { GoogleMapViewState(it, initialPosition, initialZoom) }
    MapBox -> remember {
        MapBoxMapViewState(initialPosition, initialZoom)
    }
}