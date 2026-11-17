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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.LocalThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GoogleMapViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `shows a dark google map`() {
        val state = GoogleMapViewStateFactory().create(MapProvider.Google, LatLon(52.5, 13.4), 11f)!!
        composeRule.setContent {
            CompositionLocalProvider(LocalThemeMode provides ThemeMode.DARK_MODE) {
                GoogleMapViewFactory().CreateMapView(
                    modifier = Modifier,
                    mapViewState = state,
                    mapOptions = { this },
                    contentPadding = PaddingValues(),
                    onMapClick = {},
                    content = null,
                )
            }
        }
        composeRule.waitForIdle()
    }
}
