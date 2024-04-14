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
package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions

@Composable
fun MapView(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    mapOptions: MapOptions.() -> MapOptions = { this },
    mapViewState: MapViewState,
    onMapClick: () -> Unit = {},
    content: @Composable (MapViewScope.() -> Unit)? = null,
) {
    val viewFactoryProvider = LocalMapViewFactoryProvider.current
    val mapProvider = LocalMapProvider.current
    viewFactoryProvider.getFactory(mapProvider)
        .CreateMapView(
            modifier = modifier,
            mapViewState = mapViewState,
            mapOptions = mapOptions,
            contentPadding = contentPadding,
            onMapClick = onMapClick,
            content = content,
        )
}