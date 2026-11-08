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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.ClearSelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FeedTitleTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val actions = mutableListOf<FeedAction>()

    private fun showTitle(hasSelection: Boolean) {
        composeRule.setContent {
            ComposeResourcesContext()
            FeedTitle(
                action = { actions += it },
                scrollToTop = {},
                hasSelection = hasSelection,
                selectedCelCount = 3,
            )
        }
    }

    private fun pressBack() {
        composeRule.runOnUiThread {
            composeRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeRule.waitForIdle()
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    @Test
    fun `clears the selection on back`() {
        showTitle(hasSelection = true)

        pressBack()

        assertEquals(listOf<FeedAction>(ClearSelected), actions)
    }

    @Test
    fun `clears the selection when the selection count is clicked`() {
        showTitle(hasSelection = true)

        composeRule.onNodeWithText("3").performClick()

        assertEquals(listOf<FeedAction>(ClearSelected), actions)
    }

    @Test
    fun `leaves back to the screen when nothing is selected`() {
        showTitle(hasSelection = false)

        assertFalse(composeRule.activity.onBackPressedDispatcher.hasEnabledCallbacks())
    }
}
