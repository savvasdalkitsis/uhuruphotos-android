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

import org.junit.Assert.assertEquals
import org.junit.Test
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

class PhotoPrismBasemapTest {

    private val palette = BasemapPalette.Dark

    @Test
    fun `hides the satellite imagery so the map is flat at every zoom`() {
        assertProperties("natural_earth", "raster", visibility(NONE))
    }

    @Test
    fun `shows the water at every zoom in the palette colour`() {
        assertProperties("water", "fill", fillOpacity(1f), fillColor(palette.water))
        assertProperties("water-offset", "fill", fillOpacity(1f), fillColor(palette.water))
        assertProperties("waterway-river", "line", lineColor(palette.water))
    }

    @Test
    fun `paints the land in the palette colour`() {
        assertProperties("background", "background", backgroundColor(palette.land))
    }

    @Test
    fun `keeps woods, parks and fields as a hint of colour`() {
        assertProperties("landcover-wood", "fill", fillOpacity(palette.landcoverOpacity))
        assertProperties("landuse-residential", "fill", fillOpacity(palette.landcoverOpacity))
    }

    @Test
    fun `paints ice and glaciers so they do not glare in the dark theme`() {
        assertProperties("landcover-ice-shelf", "fill", fillColor(palette.ice))
        assertProperties("landcover-glacier", "fill", fillColor(palette.ice))
    }

    @Test
    fun `paints flat and raised buildings in the palette colour`() {
        assertProperties("building-top", "fill", fillColor(palette.building))
        assertProperties("building-3d", "fill-extrusion", fillExtrusionColor(palette.building))
    }

    @Test
    fun `paints paved areas like roads`() {
        assertProperties("aeroway-area", "fill", fillColor(palette.road))
    }

    @Test
    fun `paints roads and their casings in the palette colours`() {
        assertProperties("highway-primary", "line", lineColor(palette.road))
        assertProperties("bridge-motorway-casing", "line", lineColor(palette.roadCasing))
        assertProperties("aeroway-runway", "line", lineColor(palette.road))
    }

    @Test
    fun `keeps the style's paths, railways and boundaries`() {
        assertProperties("highway-path", "line")
        assertProperties("railway", "line")
        assertProperties("boundary-land-level-2", "line")
    }

    @Test
    fun `labels the land with a thin halo of the land colour`() {
        assertProperties(
            "place-city", "symbol",
            textColor(palette.label),
            textHaloColor(palette.land),
            textHaloWidth(1f),
            textHaloBlur(0f),
        )
    }

    @Test
    fun `labels the water with a thin halo of the water colour`() {
        assertProperties(
            "water-name-ocean", "symbol",
            textColor(palette.waterLabel),
            textHaloColor(palette.water),
            textHaloWidth(1f),
            textHaloBlur(0f),
        )
    }

    @Test
    fun `keeps the road shields readable on their own background`() {
        assertProperties("highway-shield-us-other", "symbol")
    }

    @Test
    fun `leaves the layers it does not know alone`() {
        assertProperties("hillshading", "raster")
        assertProperties("heatMap", "heatmap")
    }

    @Test
    fun `paints the light theme with the LibrePhotos web basemap colours`() {
        assertEquals(
            BasemapPalette(
                land = "#f3f2ef",
                water = "#c3daef",
                label = "#5c5f66",
                waterLabel = "#4f7399",
                road = "#ffffff",
                roadCasing = "#dcd9d3",
                building = "#e4e2de",
                ice = "#ffffff",
                landcoverOpacity = 0.35f,
            ),
            BasemapPalette.Light,
        )
    }

    @Test
    fun `paints the dark theme with the night colours of the Google map users knew`() {
        assertEquals(
            BasemapPalette(
                land = "#242f3e",
                water = "#17263c",
                label = "#d59563",
                waterLabel = "#515c6d",
                road = "#38414e",
                roadCasing = "#212a37",
                building = "#2b3544",
                ice = "#2f3948",
                landcoverOpacity = 0.15f,
            ),
            BasemapPalette.Dark,
        )
    }

    private fun assertProperties(id: String, type: String, vararg expected: PropertyValue<*>) {
        assertEquals(expected.toList(), basemapProperties(id, type, palette))
    }
}
