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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode

@Composable
fun MapLibreMapView(
    modifier: Modifier,
    mapViewState: MapLibreMapViewState,
    contentPadding: PaddingValues,
    mapOptions: MapOptions.() -> MapOptions,
    onMapClick: () -> Unit,
    content: @Composable (MapViewScope.() -> Unit)?,
) {
    val setup = MapLibreMapSetup(
        state = mapViewState,
        options = mapOptions(MapOptions()),
        attributionMargins = contentPadding.toAttributionMargins(),
        palette = if (LocalThemeMode.current.isDark()) BasemapPalette.Dark else BasemapPalette.Light,
        onMapClick = onMapClick,
    )
    val createMapView = LocalMapLibreViewFactory.current
    AndroidView(
        modifier = modifier,
        factory = { context -> createMapView(context, setup) },
    )
    content?.invoke(mapViewState)
}

@Composable
private fun PaddingValues.toAttributionMargins(): AttributionMargins {
    val layoutDirection = LocalLayoutDirection.current
    return with(LocalDensity.current) {
        AttributionMargins(
            start = calculateStartPadding(layoutDirection).toPx().toInt(),
            top = calculateTopPadding().toPx().toInt(),
            end = calculateEndPadding(layoutDirection).toPx().toInt(),
            bottom = calculateBottomPadding().toPx().toInt(),
        )
    }
}
