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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.maplibre.android.BuildConfig

class MapLibreReleaseTest {

    @Test
    fun `draws heatmaps with a MapLibre release that no longer crashes updating heatmap tiles`() {
        val version = BuildConfig.MAPLIBRE_VERSION_STRING.substringAfter('/')
            .split('.')
            .map { it.toInt() }

        assertTrue(
            "MapLibre $version crashes in RenderHeatmapLayer::update before 13.1.0",
            compareValuesBy(version, listOf(13, 1, 0), { it[0] }, { it[1] }, { it[2] }) >= 0,
        )
    }

    @Test
    fun `renders with OpenGL ES so the OpenGL ES fallback still applies`() {
        assertEquals("opengl", BuildConfig.FLAVOR)
    }
}
