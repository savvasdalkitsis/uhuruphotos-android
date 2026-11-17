/*
Copyright 2026 Savvas Dalkitsis

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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

private val heatmapColors = listOf(
    Color(0, 255, 255, 0),
    Color(0, 255, 255, 255),
    Color(0, 191, 255, 255),
    Color(0, 127, 255, 255),
    Color(0, 63, 255, 255),
    Color(0, 0, 255, 255),
    Color(0, 0, 223, 255),
    Color(0, 0, 191, 255),
    Color(0, 0, 159, 255),
    Color(0, 0, 127, 255),
    Color(63, 0, 91, 255),
    Color(127, 0, 63, 255),
    Color(191, 0, 31, 255),
    Color(255, 0, 0, 255)
).map { it.toArgb() }.toIntArray()

private val heatmapGradient = Gradient(
    heatmapColors,
    FloatArray(heatmapColors.size) { it / (heatmapColors.size - 1f) },
)

internal fun heatmapTileProvider(points: Collection<LatLon>): HeatmapTileProvider =
    HeatmapTileProvider.Builder()
        .data(points.map { it.toLatLng })
        .gradient(heatmapGradient)
        .opacity(1.0)
        .build()
