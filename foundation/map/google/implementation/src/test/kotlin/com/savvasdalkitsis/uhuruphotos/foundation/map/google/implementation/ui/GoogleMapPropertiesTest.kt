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

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.maps.android.compose.MapProperties
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleMapPropertiesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `styles the map dark in dark mode`() {
        assertNotNull(googleMapProperties(context, enableMyLocation = false, isDark = true).mapStyleOptions)
    }

    @Test
    fun `keeps the default map style in light mode`() {
        assertNull(googleMapProperties(context, enableMyLocation = false, isDark = false).mapStyleOptions)
    }

    @Test
    fun `shows the user location when asked to`() {
        assertEquals(true, googleMapProperties(context, enableMyLocation = true, isDark = false).isMyLocationEnabled)
    }

    @Test
    fun `remembers dark styled map properties`() {
        val properties = mutableListOf<MapProperties>()
        composeRule.setContent {
            CompositionLocalProvider(LocalThemeMode provides ThemeMode.DARK_MODE) {
                properties += rememberGoogleMapProperties(enableMyLocation = false, isDark = true)
            }
        }
        composeRule.waitForIdle()

        assertNotNull(properties.single().mapStyleOptions)
    }
}
