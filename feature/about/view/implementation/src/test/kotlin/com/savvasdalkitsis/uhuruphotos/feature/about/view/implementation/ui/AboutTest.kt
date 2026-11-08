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
package com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mikepenz.aboutlibraries.Libs
import com.savvasdalkitsis.uhuruphotos.feature.about.view.implementation.ui.state.AboutState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import io.mockk.mockk
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AboutTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val libraries = Libs.Builder().withJson(
        """
        {
          "libraries": [
            {
              "uniqueId": "com.example:sample",
              "artifactVersion": "1.0.0",
              "name": "Sample Library",
              "developers": [{ "name": "Sample Developer" }],
              "licenses": ["Apache-2.0"]
            }
          ],
          "licenses": {
            "Apache-2.0": {
              "name": "Apache License 2.0",
              "url": "https://www.apache.org/licenses/LICENSE-2.0",
              "content": "Sample licence text",
              "hash": "Apache-2.0"
            }
          }
        }
        """.trimIndent()
    ).build()

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    @Test
    fun `opens the licence dialog when a library is clicked`() {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(LocalNavigator provides mockk(relaxed = true)) {
                About(state = AboutState("0.0.999"), libraries = libraries, action = {})
            }
        }

        composeRule.onNodeWithText("Sample Library").performClick()

        composeRule.onNodeWithText("Sample licence text").assertIsDisplayed()
    }

    @Test
    fun `lists the libraries bundled with the app`() {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(LocalNavigator provides mockk(relaxed = true)) {
                About(state = AboutState("0.0.999"), action = {})
            }
        }

        composeRule.waitUntil { composeRule.onAllNodesWithText("Generated Library").fetchSemanticsNodes().isNotEmpty() }
    }
}
