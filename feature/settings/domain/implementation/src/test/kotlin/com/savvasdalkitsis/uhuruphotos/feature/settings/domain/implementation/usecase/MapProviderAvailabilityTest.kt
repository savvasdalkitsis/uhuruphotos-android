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
package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.MapBox
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider.MapLibre
import org.junit.Assert.assertEquals
import org.junit.Test

class MapProviderAvailabilityTest {

    @Test
    fun `keeps every provider on a device with Play services and OpenGL ES 3`() {
        MapProvider.entries.forEach { provider ->
            assertEquals(provider, resolve(provider, playServices = true, openGlEs3 = true))
        }
    }

    @Test
    fun `falls back from Google to MapLibre without Play services`() {
        assertEquals(MapLibre, resolve(Google, playServices = false, openGlEs3 = true))
    }

    @Test
    fun `falls back from MapBox to Google without OpenGL ES 3`() {
        assertEquals(Google, resolve(MapBox, playServices = true, openGlEs3 = false))
    }

    @Test
    fun `falls back from MapLibre to Google without OpenGL ES 3`() {
        assertEquals(Google, resolve(MapLibre, playServices = true, openGlEs3 = false))
    }

    @Test
    fun `never picks MapBox without Play services or OpenGL ES 3`() {
        MapProvider.entries.forEach { provider ->
            assertEquals(MapLibre, resolve(provider, playServices = false, openGlEs3 = false))
        }
    }

    private fun resolve(
        requested: MapProvider,
        playServices: Boolean,
        openGlEs3: Boolean,
    ): MapProvider = MapProviderAvailability(
        object : MapCapabilities {
            override fun hasGooglePlayServices() = playServices
            override fun supportsOpenGlEs3() = openGlEs3
        }
    ).resolve(requested)
}
