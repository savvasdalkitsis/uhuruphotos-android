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
import com.google.maps.android.compose.CameraPositionState
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapViewState

internal class GoogleMapViewState(
    internal val cameraPositionState: CameraPositionState,
    override val initialPosition: LatLon,
    override val initialZoom: Float,
) : MapViewState {

    override val isMoving: State<Boolean>
        @Composable
        get() = produceState(cameraPositionState.isMoving) { value = cameraPositionState.isMoving }
    override val markers: MutableState<Set<LatLon>> = mutableStateOf(emptySet())
    override val heatMapPoints: MutableState<Set<LatLon>> = mutableStateOf(emptySet())

    override fun contains(latLon: LatLon) = cameraPositionState
        .projection
        ?.visibleRegion
        ?.latLngBounds
        ?.contains(latLon.toLatLng)
        ?: false
}