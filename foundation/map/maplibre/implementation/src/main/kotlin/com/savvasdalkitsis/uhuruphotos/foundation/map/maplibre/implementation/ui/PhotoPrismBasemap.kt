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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import org.maplibre.android.style.layers.Property.NONE
import org.maplibre.android.style.layers.PropertyFactory.backgroundColor
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillExtrusionColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.textColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloBlur
import org.maplibre.android.style.layers.PropertyFactory.textHaloColor
import org.maplibre.android.style.layers.PropertyFactory.textHaloWidth
import org.maplibre.android.style.layers.PropertyFactory.visibility
import org.maplibre.android.style.layers.PropertyValue

internal data class BasemapPalette(
    val land: String,
    val water: String,
    val label: String,
    val waterLabel: String,
    val road: String,
    val roadCasing: String,
    val building: String,
    val ice: String,
    val landcoverOpacity: Float,
) {
    companion object {
        val Light = BasemapPalette(
            land = "#f3f2ef",
            water = "#c3daef",
            label = "#5c5f66",
            waterLabel = "#4f7399",
            road = "#ffffff",
            roadCasing = "#dcd9d3",
            building = "#e4e2de",
            ice = "#ffffff",
            landcoverOpacity = 0.35f,
        )
        val Dark = BasemapPalette(
            land = "#242f3e",
            water = "#17263c",
            label = "#d59563",
            waterLabel = "#515c6d",
            road = "#38414e",
            roadCasing = "#212a37",
            building = "#2b3544",
            ice = "#2f3948",
            landcoverOpacity = 0.15f,
        )
    }
}

private const val IMAGERY = "natural_earth"
private val ICE = Regex("^landcover-(ice|glacier)")
private val LANDCOVER = Regex("^(landcover|landuse)-")
private val ROAD = Regex("^((highway|bridge|tunnel)-(motorway|link|trunk|primary|secondary|minor|service)|aeroway-)")
private val WATER_NAME = Regex("^water(way)?-name")

internal fun basemapProperties(id: String, type: String, palette: BasemapPalette): List<PropertyValue<*>> =
    when (type) {
        "background" -> listOf(backgroundColor(palette.land))
        "raster" -> imageryProperties(id)
        "fill" -> fillProperties(id, palette)
        "fill-extrusion" -> listOf(fillExtrusionColor(palette.building))
        "line" -> lineProperties(id, palette)
        "symbol" -> labelProperties(id, palette)
        else -> emptyList()
    }

private fun imageryProperties(id: String): List<PropertyValue<*>> =
    if (id == IMAGERY) listOf(visibility(NONE)) else emptyList()

private fun fillProperties(id: String, palette: BasemapPalette): List<PropertyValue<*>> = when {
    id.startsWith("water") -> listOf(fillOpacity(1f), fillColor(palette.water))
    ICE.containsMatchIn(id) -> listOf(fillColor(palette.ice))
    LANDCOVER.containsMatchIn(id) -> listOf(fillOpacity(palette.landcoverOpacity))
    id.startsWith("building") -> listOf(fillColor(palette.building))
    else -> listOf(fillColor(palette.road))
}

private fun lineProperties(id: String, palette: BasemapPalette): List<PropertyValue<*>> = when {
    id.startsWith("waterway") -> listOf(lineColor(palette.water))
    !ROAD.containsMatchIn(id) -> emptyList()
    id.endsWith("-casing") -> listOf(lineColor(palette.roadCasing))
    else -> listOf(lineColor(palette.road))
}

private fun labelProperties(id: String, palette: BasemapPalette): List<PropertyValue<*>> {
    if (id.contains("shield")) return emptyList()
    val onWater = WATER_NAME.containsMatchIn(id)
    return listOf(
        textColor(if (onWater) palette.waterLabel else palette.label),
        textHaloColor(if (onWater) palette.water else palette.land),
        textHaloWidth(1f),
        textHaloBlur(0f),
    )
}
