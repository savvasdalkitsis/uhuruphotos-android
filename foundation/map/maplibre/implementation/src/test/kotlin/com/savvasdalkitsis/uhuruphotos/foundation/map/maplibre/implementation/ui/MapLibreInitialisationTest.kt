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

import android.content.Context
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.maplibre.android.maps.MapView

class MapLibreInitialisationTest {

    private val context = mockk<Context>()
    private val mapView = mockk<MapView>()
    private val events = mutableListOf<Pair<String, Context>>()

    @Test
    fun `initialises maplibre before creating a map view`() {
        val created = newMapLibreView(
            context,
            initialize = { events += "initialise" to it },
            create = {
                events += "create" to it
                mapView
            },
        )

        assertEquals(listOf("initialise" to context, "create" to context), events)
        assertSame(mapView, created)
    }
}
