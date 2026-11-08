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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapLibreMapViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val state = MapLibreMapViewState(LatLon(52.5, 13.4), 11f)
    private val setups = mutableListOf<MapLibreMapSetup>()
    private var scope: MapViewScope? = null
    private var clicks = 0

    @Test
    fun `sets the map up with the dark basemap in dark mode`() {
        show(ThemeMode.DARK_MODE)

        assertEquals(BasemapPalette.Dark, setups.single().palette)
    }

    @Test
    fun `sets the map up with the light basemap in light mode`() {
        show(ThemeMode.LIGHT_MODE)

        assertEquals(BasemapPalette.Light, setups.single().palette)
    }

    @Test
    fun `keeps the attribution clear of the content padding`() {
        show(ThemeMode.LIGHT_MODE)

        assertEquals(
            AttributionMargins(start = 20, top = 40, end = 60, bottom = 80),
            setups.single().attributionMargins,
        )
    }

    @Test
    fun `applies the requested map options`() {
        show(ThemeMode.LIGHT_MODE)

        assertEquals(MapOptions(scrollGesturesEnabled = true), setups.single().options)
    }

    @Test
    fun `hands the map state and click listener to the map`() {
        show(ThemeMode.LIGHT_MODE)
        setups.single().onMapClick()

        assertSame(state, setups.single().state)
        assertEquals(1, clicks)
    }

    @Test
    fun `lets the content draw on the map state`() {
        show(ThemeMode.LIGHT_MODE)

        assertSame(state, scope)
    }

    private fun show(themeMode: ThemeMode) {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalThemeMode provides themeMode,
                LocalDensity provides Density(2f),
                LocalMapLibreViewFactory provides { context, setup ->
                    setups += setup
                    View(context)
                },
            ) {
                MapLibreMapView(
                    modifier = Modifier,
                    mapViewState = state,
                    contentPadding = PaddingValues(start = 10.dp, top = 20.dp, end = 30.dp, bottom = 40.dp),
                    mapOptions = { copy(scrollGesturesEnabled = true) },
                    onMapClick = { clicks++ },
                    content = { scope = this },
                )
            }
        }
        composeRule.waitForIdle()
    }
}
