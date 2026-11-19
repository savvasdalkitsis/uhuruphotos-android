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

import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.exceptions.CalledFromWorkerThreadException
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.geometry.VisibleRegion
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class MapLibreMapViewStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val berlin = LatLon(52.5, 13.4)
    private val munich = LatLon(48.1, 11.6)
    private val points = setOf(berlin, munich)
    private val mapView = mockk<MapView>(relaxed = true)
    private val state = MapLibreMapViewState(berlin, 11f)
    private var recompositions by mutableIntStateOf(0)
    private var markersRemoved = 0
    private val map = mockk<MapLibreMap>(relaxed = true)
    private var camera = CameraPosition.Builder().target(berlin.toLatLng).zoom(11.0).build()

    @Before
    fun interceptMapDrawing() {
        mockkStatic(EXTENSIONS)
        every { mapView.addMarker(any()) } returns { markersRemoved++ }
        every { mapView.showHeatMap(any()) } returns Unit
    }

    @After
    fun restoreMapDrawing() {
        unmockkStatic(EXTENSIONS)
    }

    @Test
    fun `places the marker once however often the map recomposes`() {
        state.mapView = mapView
        showOnMap { state.Marker(berlin) }

        recomposeTwice()

        verify(exactly = 1) { mapView.addMarker(berlin) }
    }

    @Test
    fun `places the marker once the map view is attached`() {
        showOnMap { state.Marker(berlin) }

        state.mapView = mapView
        composeRule.waitForIdle()

        verify(exactly = 1) { mapView.addMarker(berlin) }
    }

    @Test
    fun `moves the marker when its location changes`() {
        var location by mutableStateOf(berlin)
        state.mapView = mapView
        showOnMap { state.Marker(location) }

        location = munich
        composeRule.waitForIdle()

        verify(exactly = 1) { mapView.addMarker(munich) }
        assertEquals(1, markersRemoved)
    }

    @Test
    fun `draws the heat map once however often the map recomposes`() {
        state.mapView = mapView
        showOnMap { state.HeatMap(points, points) }

        recomposeTwice()

        verify(exactly = 1) { mapView.showHeatMap(points) }
    }

    @Test
    fun `draws the heat map once the map view is attached`() {
        showOnMap { state.HeatMap(points, points) }

        state.mapView = mapView
        composeRule.waitForIdle()

        verify(exactly = 1) { mapView.showHeatMap(points) }
    }

    @Test
    fun `draws no heat map without points on the visible map`() {
        state.mapView = mapView
        showOnMap { state.HeatMap(points, emptySet()) }

        verify(exactly = 0) { mapView.showHeatMap(any()) }
    }

    @Test
    fun `centers the map on the ui thread when asked from a worker thread`() {
        state.mapView = mapView
        mapMovingOnlyOnTheUiThread()

        fromWorkerThread { state.centerToLocation(munich) }

        assertEquals(munich.toLatLng, camera.target)
    }

    private fun mapMovingOnlyOnTheUiThread() {
        every { mapView.getMapAsync(any()) } answers { firstArg<OnMapReadyCallback>().onMapReady(map) }
        every { map.cameraPosition } answers { camera }
        every { map.cameraPosition = any() } answers {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw CalledFromWorkerThreadException("Map interactions should happen on the UI thread.")
            }
            camera = firstArg()
        }
        every { map.projection.visibleRegion } returns visibleRegion()
    }

    private fun visibleRegion(): VisibleRegion {
        val bounds = LatLngBounds.from(53.0, 14.0, 48.0, 11.0)
        return VisibleRegion(bounds.northWest, bounds.northEast, bounds.southWest, bounds.southEast, bounds)
    }

    private fun fromWorkerThread(call: suspend () -> Unit) {
        val result = CoroutineScope(Dispatchers.Default).async { call() }
        while (result.isActive) shadowOf(Looper.getMainLooper()).idle()
        runBlocking { result.await() }
    }

    private fun showOnMap(content: @Composable () -> Unit) {
        composeRule.setContent {
            recompositions
            content()
        }
        composeRule.waitForIdle()
    }

    private fun recomposeTwice() {
        repeat(2) {
            recompositions++
            composeRule.waitForIdle()
        }
    }

    private companion object {
        const val EXTENSIONS = "com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui.MapLibreExtensionsKt"
    }
}
