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
package com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapViewFactoryProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LocalMapViewStateFactory
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapViewStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val provider = mutableStateOf(MapProvider.MapLibre)
    private val shownBy = mutableListOf<MapProvider>()

    @Test
    fun `recreates the map state for the provider the map is drawn with`() {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalMapProvider provides provider.value,
                LocalMapViewStateFactory provides CompositeMapViewStateFactory(setOf(ProviderStateFactory)),
                LocalMapViewFactoryProvider provides CompositeMapViewFactoryProvider(setOf(castingFactories)),
            ) {
                MapView(mapViewState = rememberMapViewState(LatLon(52.5, 13.4), 11f))
            }
        }
        composeRule.waitForIdle()

        provider.value = MapProvider.Google
        composeRule.waitForIdle()

        assertEquals(listOf(MapProvider.MapLibre, MapProvider.Google), shownBy)
    }

    private val castingFactories = MapViewFactoryProvider { mapProvider ->
        MapViewFactory { _, mapViewState, _, _, _, _ ->
            shownBy += when (mapProvider) {
                MapProvider.Google -> (mapViewState as GoogleState).provider
                MapProvider.MapBox -> (mapViewState as MapBoxState).provider
                MapProvider.MapLibre -> (mapViewState as MapLibreState).provider
            }
        }
    }

    private object ProviderStateFactory : MapViewStateFactory {
        override fun create(mapProvider: MapProvider, initialPosition: LatLon, initialZoom: Float) =
            when (mapProvider) {
                MapProvider.Google -> GoogleState(initialPosition, initialZoom)
                MapProvider.MapBox -> MapBoxState(initialPosition, initialZoom)
                MapProvider.MapLibre -> MapLibreState(initialPosition, initialZoom)
            }
    }

    private class GoogleState(position: LatLon, zoom: Float) : FakeState(MapProvider.Google, position, zoom)
    private class MapBoxState(position: LatLon, zoom: Float) : FakeState(MapProvider.MapBox, position, zoom)
    private class MapLibreState(position: LatLon, zoom: Float) : FakeState(MapProvider.MapLibre, position, zoom)

    private abstract class FakeState(
        val provider: MapProvider,
        override val initialPosition: LatLon,
        override val initialZoom: Float,
    ) : MapViewState {
        override fun contains(latLon: LatLon) = false
        override suspend fun centerToLocation(latLon: LatLon) = Unit

        @Composable
        override fun Composition(onStoppedMoving: (Viewport) -> Unit) = Unit

        @Composable
        override fun Marker(latLon: LatLon) = Unit

        @Composable
        override fun HeatMap(allPoints: Collection<LatLon>, pointsOnVisibleMap: Collection<LatLon>) = Unit
    }
}
