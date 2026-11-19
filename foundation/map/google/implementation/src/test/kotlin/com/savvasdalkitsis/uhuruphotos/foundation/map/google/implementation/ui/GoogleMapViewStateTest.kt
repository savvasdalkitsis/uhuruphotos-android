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

import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.android.gms.maps.model.TileProvider
import com.google.maps.android.compose.CameraPositionState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleMapViewStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val berlin = LatLon(52.5, 13.4)
    private val munich = LatLon(48.1, 11.6)
    private val overlays = mutableListOf<TileProvider>()
    private val state = GoogleMapViewState(CameraPositionState(), berlin, 11f) { overlays += it }
    private var recompositions by mutableIntStateOf(0)
    private var points by mutableStateOf(listOf(berlin, munich))

    @Test
    fun `keeps the heat map tiles however often the map recomposes`() {
        showHeatMap()

        recomposeTwice()

        assertEquals(3, overlays.size)
        assertEquals(1, overlays.distinct().size)
    }

    @Test
    fun `redraws the heat map tiles when the points change`() {
        showHeatMap()

        points = listOf(munich)
        composeRule.waitForIdle()
        composeRule.waitUntil(5000) { overlays.distinct().size == 2 }

        assertEquals(2, overlays.distinct().size)
    }

    @Test
    fun `builds the heat map tiles off the main thread`() {
        val builtOn = mutableListOf<Thread>()
        mockkStatic(TILES)
        every { heatmapTileProvider(any()) } answers {
            builtOn += Thread.currentThread()
            callOriginal()
        }

        showHeatMap()
        unmockkStatic(TILES)

        assertEquals(1, builtOn.size)
        assertNotEquals(Looper.getMainLooper().thread, builtOn.single())
    }

    @Test
    fun `draws no heat map without points on the visible map`() {
        points = emptyList()
        showHeatMap()

        assertEquals(emptyList<TileProvider>(), overlays)
    }

    private fun showHeatMap() {
        composeRule.setContent {
            recompositions
            state.HeatMap(points.toList(), points.toList())
        }
        composeRule.waitForIdle()
        composeRule.waitUntil(5000) { points.isEmpty() || overlays.isNotEmpty() }
    }

    private fun recomposeTwice() {
        repeat(2) {
            recompositions++
            composeRule.waitForIdle()
        }
    }

    private companion object {
        const val TILES = "com.savvasdalkitsis.uhuruphotos.foundation.map.google.implementation.ui.HeatmapTilesKt"
    }
}
