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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.map.model.MapOptions

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    location: LatLon,
    zoom: Float = 10f,
    contentPadding: PaddingValues = PaddingValues(),
    mapOptions: MapOptions.() -> MapOptions = { this },
    onMapClick: (LatLon) -> Unit = {},
    content: @Composable (MapViewScope.() -> Unit)? = null,
) {
    val mapViewState = rememberGoogleMapViewState(
        initialPosition = location,
        zoom = zoom,
    )
    GoogleMapView(
        modifier = modifier,
        mapViewState = mapViewState,
        mapOptions = mapOptions,
        contentPadding = contentPadding,
        onMapClick = onMapClick,
        content = content,
    )
}