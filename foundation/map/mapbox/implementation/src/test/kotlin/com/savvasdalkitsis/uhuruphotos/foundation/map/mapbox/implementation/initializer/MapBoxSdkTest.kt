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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer

import android.content.Context
import com.mapbox.maps.MapView
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui.MapBoxMapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui.newMapView
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Test

class MapBoxSdkTest {

    private val context = mockk<Context>()
    private val events = mutableListOf<String>()
    private val sdk = MapBoxSdk(context, { events += "load" }, Dispatchers.Unconfined)
    private val stateFactory = MapBoxMapViewStateFactory(sdk)
    private val berlin = LatLon(52.5, 13.4)

    @Test
    fun `does not load mapbox until a mapbox map is used`() {
        MapBoxSdk(context, { events += "load" }, Dispatchers.Unconfined)

        assertEquals(emptyList<String>(), events)
    }

    @Test
    fun `does not load mapbox for maps from other providers`() {
        val states = listOf(MapProvider.MapLibre, MapProvider.Google).map {
            stateFactory.create(it, berlin, 11f)
        }

        assertEquals(listOf(null, null), states)
        assertEquals(emptyList<String>(), events)
    }

    @Test
    fun `starts loading mapbox as soon as a mapbox map is prepared`() {
        stateFactory.create(MapProvider.MapBox, berlin, 11f)

        assertEquals(listOf("load"), events)
    }

    @Test
    fun `loads mapbox and its access token before creating a map view`() {
        val mapView = mockk<MapView>()

        sdk.newMapView(context) {
            events += "create"
            mapView
        }

        assertEquals(listOf("load", "create"), events)
    }

    @Test
    fun `loads mapbox only once`() {
        stateFactory.create(MapProvider.MapBox, berlin, 11f)
        repeat(2) { sdk.newMapView(context) { mockk() } }

        assertEquals(listOf("load"), events)
    }

    @Test
    fun `retries loading when a background load failed`() {
        var attempts = 0
        val failingOnce = MapBoxSdk(context, { if (attempts++ == 0) error("no native library") }, Dispatchers.Unconfined)

        failingOnce.preload()
        failingOnce.ensureLoaded()

        assertEquals(2, attempts)
    }
}
