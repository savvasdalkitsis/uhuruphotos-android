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

import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.extensions.getActivity
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapLibreMapReleaseTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var showMap by mutableStateOf(true)
    private val events = mutableListOf<String>()
    private var observersWithMap = -1
    private lateinit var lifecycle: LifecycleRegistry
    private val mapLifecycle = object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) { events += "pause" }
        override fun onStop(owner: LifecycleOwner) { events += "stop" }
        override fun onDestroy(owner: LifecycleOwner) { events += "destroy" }
    }

    private fun showThenDispose() {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalThemeMode provides ThemeMode.LIGHT_MODE,
                LocalMapLibreViewFactory provides { context, _ ->
                    lifecycle = context.getActivity()!!.lifecycle as LifecycleRegistry
                    lifecycle.addObserver(mapLifecycle)
                    View(context).apply { tag = mapLifecycle }
                },
            ) {
                if (showMap) {
                    MapLibreMapView(
                        modifier = Modifier,
                        mapViewState = MapLibreMapViewState(LatLon(52.5, 13.4), 11f),
                        contentPadding = PaddingValues(),
                        mapOptions = { this },
                        onMapClick = {},
                        content = null,
                    )
                }
            }
        }
        composeRule.waitForIdle()
        observersWithMap = lifecycle.observerCount
        events.clear()
        showMap = false
        composeRule.waitForIdle()
    }

    @Test
    fun `stops and destroys the map once it leaves the composition`() {
        showThenDispose()

        assertEquals(listOf("pause", "stop", "destroy"), events)
    }

    @Test
    fun `stops following the activity lifecycle once the map leaves the composition`() {
        showThenDispose()

        assertEquals(observersWithMap - 1, lifecycle.observerCount)
    }
}
