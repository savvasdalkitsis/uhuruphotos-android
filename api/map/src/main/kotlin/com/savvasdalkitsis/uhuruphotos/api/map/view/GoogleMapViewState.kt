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

import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon

class GoogleMapViewState(
    internal val cameraPositionState: CameraPositionState,
) : MapViewState {

    override val isMoving get() = cameraPositionState.isMoving
    override fun contains(latLon: LatLon) = cameraPositionState
        .projection
        ?.visibleRegion
        ?.latLngBounds
        ?.contains(latLon.toLatLng)
        ?: false
}

@Composable
fun rememberGoogleMapViewState(
    initialPosition: LatLon,
    zoom: Float,
): GoogleMapViewState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(initialPosition.toLatLng, zoom)
}.let { GoogleMapViewState(it) }