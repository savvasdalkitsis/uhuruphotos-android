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
package com.savvasdalkitsis.uhuruphotos.map.view

import androidx.compose.runtime.Composable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.savvasdalkitsis.uhuruphotos.map.model.LatLon

internal class GoogleMapsViewScope : MapViewScope {

    @Composable
    override fun Marker(latLon: LatLon) {
        Marker(
            state = MarkerState(position = latLon.toLatLng),
        )
    }

    @Composable
    override fun HeatMap(points: Collection<LatLon>) {
        if (points.isNotEmpty()) {
            TileOverlay(
                tileProvider = HeatmapTileProvider.Builder()
                    .data(points.map { it.toLatLng })
                    .build()
            )
        }
    }
}