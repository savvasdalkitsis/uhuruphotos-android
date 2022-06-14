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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.mapbox.maps.CoordinateBounds
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.map.view.MapViewState

internal class MapBoxMapViewState(
    override val initialPosition: LatLon,
    override val initialZoom: Float,
) : MapViewState {
    var bounds: CoordinateBounds? = null
    var _moving = mutableStateOf(false)
    override val isMoving: State<Boolean> @Composable get() = _moving
    override val markers: MutableState<Set<LatLon>> = mutableStateOf(emptySet())
    override val heatMapPoints: MutableState<Set<LatLon>> = mutableStateOf(emptySet())
    override fun contains(latLon: LatLon): Boolean {
        val contains = bounds?.contains(latLon.toPoint, false)
        return contains == true
    }
}