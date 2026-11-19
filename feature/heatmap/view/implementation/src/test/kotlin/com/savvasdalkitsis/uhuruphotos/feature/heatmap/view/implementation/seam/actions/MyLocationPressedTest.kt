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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.CancellationSignal
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.currentLocationTimeout
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.function.Consumer

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MyLocationPressedTest {

    private val consumers = mutableMapOf<String, Consumer<Location?>>()
    private val signals = mutableMapOf<String, CancellationSignal>()
    private val locationManager = mockk<LocationManager>(relaxed = true) {
        every { getCurrentLocation(any<String>(), any(), any(), any()) } answers {
            consumers[firstArg()] = arg(3)
            signals[firstArg()] = secondArg()
        }
        every { getLastKnownLocation(any()) } returns null
    }
    private val mapViewState = mockk<MapViewState>(relaxed = true)
    private val context = HeatMapActionsContext(mockk(), mockk(), mockk(), mockk(), locationManager, mockk(), mockk())

    @Test
    fun `centers the map on a location that arrives after it was requested`() = runTest {
        givenEnabled(NETWORK_PROVIDER)
        val pressed = launch { press() }
        runCurrent()

        consumers.getValue(NETWORK_PROVIDER).accept(location(52.5, 13.4))
        pressed.join()

        coVerify { mapViewState.centerToLocation(LatLon(52.5, 13.4)) }
    }

    @Test
    fun `centers on the first provider to answer and stops the others`() = runTest {
        givenEnabled(NETWORK_PROVIDER, GPS_PROVIDER)
        val pressed = launch { press() }
        runCurrent()

        consumers.getValue(GPS_PROVIDER).accept(location(48.1, 11.6))
        pressed.join()

        coVerify(exactly = 1) { mapViewState.centerToLocation(any()) }
        coVerify { mapViewState.centerToLocation(LatLon(48.1, 11.6)) }
        assertTrue(signals.getValue(NETWORK_PROVIDER).isCanceled)
    }

    @Test
    fun `falls back to the most recent known location when no provider answers in time`() = runTest {
        givenEnabled(NETWORK_PROVIDER, GPS_PROVIDER)
        every { locationManager.getLastKnownLocation(NETWORK_PROVIDER) } returns location(1.0, 1.0, time = 1)
        every { locationManager.getLastKnownLocation(GPS_PROVIDER) } returns location(2.0, 2.0, time = 2)
        val pressed = launch { press() }

        advanceTimeBy(currentLocationTimeout)
        runCurrent()

        assertTrue(pressed.isCompleted)
        coVerify { mapViewState.centerToLocation(LatLon(2.0, 2.0)) }
    }

    @Test
    fun `does nothing and finishes when no location is known in time`() = runTest {
        givenEnabled(NETWORK_PROVIDER)
        val pressed = launch { press() }

        advanceTimeBy(currentLocationTimeout)
        runCurrent()

        assertTrue(pressed.isCompleted)
        coVerify(exactly = 0) { mapViewState.centerToLocation(any()) }
    }

    @Test
    fun `does nothing when no provider can locate the device`() = runTest {
        press()

        coVerify(exactly = 0) { mapViewState.centerToLocation(any()) }
    }

    private suspend fun press() =
        MyLocationPressed(mapViewState).run { context.handle(HeatMapState()) }.collect()

    private fun givenEnabled(vararg providers: String) = providers.forEach {
        every { locationManager.hasProvider(it) } returns true
        every { locationManager.isProviderEnabled(it) } returns true
    }

    private fun location(lat: Double, lon: Double, time: Long = 0) = Location(NETWORK_PROVIDER).apply {
        latitude = lat
        longitude = lon
        this.time = time
    }
}
