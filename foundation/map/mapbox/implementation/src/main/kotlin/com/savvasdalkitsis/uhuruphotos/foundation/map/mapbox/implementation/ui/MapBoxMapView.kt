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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer.MapBoxSdk
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode

@Composable
internal fun MapBoxMapView(
    sdk: MapBoxSdk,
    modifier: Modifier,
    mapViewState: MapBoxMapViewState,
    mapOptions: MapOptions.() -> MapOptions,
    onMapClick: () -> Unit,
    content: @Composable (MapViewScope.() -> Unit)?,
) {
    val setup = MapBoxMapSetup(
        sdk = sdk,
        state = mapViewState,
        options = mapOptions(MapOptions()),
        isDark = LocalThemeMode.current.isDark(),
        onMapClick = onMapClick,
    )
    val createMapView = LocalMapBoxViewFactory.current
    AndroidView(
        modifier = modifier,
        factory = { context -> createMapView(context, setup) },
    )
    content?.invoke(mapViewState)
}
