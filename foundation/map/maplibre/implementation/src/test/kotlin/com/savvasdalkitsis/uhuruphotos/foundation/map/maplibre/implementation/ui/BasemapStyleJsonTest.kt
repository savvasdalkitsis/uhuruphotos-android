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

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

class BasemapStyleJsonTest {

    private val palette = BasemapPalette.Dark
    private val style = """
        {
          "version": 8,
          "sprite": "https://cdn.photoprism.app/maps/default/sprite",
          "layers": [
            {"id": "background", "type": "background", "paint": {"background-color": "#FCF7E9"}},
            {"id": "natural_earth", "type": "raster", "source": "natural_earth"},
            {"id": "water", "type": "fill", "paint": {"fill-color": "#66b4ee", "fill-outline-color": "#000"}},
            {"id": "place-city", "type": "symbol", "layout": {"text-field": "{name}"}}
          ]
        }
    """.trimIndent()

    @Test
    fun `repaints the layers the basemap restyles`() {
        assertEquals(palette.land, paint("background")["background-color"].asString)
        assertEquals(palette.water, paint("water")["fill-color"].asString)
        assertEquals(1f, paint("water")["fill-opacity"].asFloat)
    }

    @Test
    fun `keeps the paint the basemap does not restyle`() {
        assertEquals("#000", paint("water")["fill-outline-color"].asString)
    }

    @Test
    fun `adds paint to layers that had none`() {
        assertEquals(palette.label, paint("place-city")["text-color"].asString)
        assertEquals(palette.land, paint("place-city")["text-halo-color"].asString)
    }

    @Test
    fun `hides layers through their layout`() {
        assertEquals("none", layer("natural_earth").getAsJsonObject("layout")["visibility"].asString)
    }

    @Test
    fun `keeps the rest of the style`() {
        val calm = JsonParser.parseString(calmStyleJson(style, palette)).asJsonObject

        assertEquals("https://cdn.photoprism.app/maps/default/sprite", calm["sprite"].asString)
        assertEquals("{name}", layer("place-city").getAsJsonObject("layout")["text-field"].asString)
    }

    private fun paint(id: String): JsonObject = layer(id).getAsJsonObject("paint")

    private fun layer(id: String): JsonObject =
        JsonParser.parseString(calmStyleJson(style, palette)).asJsonObject
            .getAsJsonArray("layers")
            .map { it.asJsonObject }
            .single { it["id"].asString == id }
}
