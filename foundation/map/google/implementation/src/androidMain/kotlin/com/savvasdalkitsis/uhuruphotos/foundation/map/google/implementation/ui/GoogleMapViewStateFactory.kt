/*
Copyright 2023 Savvas Dalkitsis

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

import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewStateFactory

class GoogleMapViewStateFactory : MapViewStateFactory {

    override fun create(
        mapProvider: MapProvider,
        initialPosition: LatLon,
        initialZoom: Float,
    ): MapViewState? = when (mapProvider) {
        Google -> {
            val cameraPosition =
                CameraPosition.fromLatLngZoom(initialPosition.toLatLng, initialZoom)
            GoogleMapViewState(CameraPositionState(cameraPosition), initialPosition, initialZoom)
        }
        else -> null
        }
}
