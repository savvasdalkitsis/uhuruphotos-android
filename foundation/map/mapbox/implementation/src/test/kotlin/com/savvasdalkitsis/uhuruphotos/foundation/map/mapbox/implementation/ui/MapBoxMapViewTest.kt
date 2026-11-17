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
package com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.ui

import android.view.View
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.mapbox.maps.Style
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapOptions
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewScope
import com.savvasdalkitsis.uhuruphotos.foundation.map.mapbox.implementation.initializer.MapBoxSdk
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapBoxMapViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sdk = mockk<MapBoxSdk>()
    private val state = MapBoxMapViewState(LatLon(52.5, 13.4), 11f)
    private val setups = mutableListOf<MapBoxMapSetup>()
    private var scope: MapViewScope? = null
    private var clicks = 0

    @Test
    fun `hands the map the sdk, state, options and click listener`() {
        show(ThemeMode.LIGHT_MODE)
        val setup = setups.single()
        setup.onMapClick()

        assertSame(sdk, setup.sdk)
        assertSame(state, setup.state)
        assertEquals(MapOptions(scrollGesturesEnabled = true), setup.options)
        assertEquals(1, clicks)
    }

    @Test
    fun `sets the map up for dark mode`() {
        show(ThemeMode.DARK_MODE)

        assertEquals(true, setups.single().isDark)
    }

    @Test
    fun `lets the content draw on the map state`() {
        show(ThemeMode.LIGHT_MODE)

        assertSame(state, scope)
    }

    @Test
    fun `shows the map without content`() {
        show(ThemeMode.LIGHT_MODE, content = null)

        assertEquals(1, setups.size)
    }

    @Test
    fun `picks the dark style in dark mode`() {
        assertEquals(Style.DARK, mapBoxStyle(isDark = true))
        assertEquals(Style.MAPBOX_STREETS, mapBoxStyle(isDark = false))
    }

    private fun show(
        themeMode: ThemeMode,
        content: @Composable (MapViewScope.() -> Unit)? = { scope = this },
    ) {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalThemeMode provides themeMode,
                LocalMapBoxViewFactory provides { context, setup ->
                    setups += setup
                    View(context)
                },
            ) {
                MapBoxMapViewFactory(sdk).CreateMapView(
                    modifier = Modifier,
                    mapViewState = state,
                    mapOptions = { copy(scrollGesturesEnabled = true) },
                    contentPadding = PaddingValues(),
                    onMapClick = { clicks++ },
                    content = content,
                )
            }
        }
        composeRule.waitForIdle()
    }
}
