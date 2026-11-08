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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.ChangeGallerySorting
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GallerySortingState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import io.mockk.mockk
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GalleryTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val longTitle = "A trip through every small village along the northern coast in late summer"
    private val actions = mutableListOf<GalleryAction>()

    private fun showGallery(state: () -> GalleryState) {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(LocalNavigator provides remember { mockk(relaxed = true) }) {
                SharedTransitionLayout {
                    Gallery(
                        modifier = Modifier,
                        titleSharedElementId = null,
                        state = state(),
                        additionalActionBarContent = {},
                        emptyContent = { Text("Nothing here") },
                        action = { actions += it },
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    @Test
    fun `keeps a long title on a single line`() {
        composeRule.setContent {
            CompositionLocalProvider(LocalNavigator provides mockk(relaxed = true)) {
                Box(Modifier.width(320.dp)) {
                    SharedTransitionLayout {
                        Gallery(state = GalleryState(title = Title.Text(longTitle)), action = {})
                    }
                }
            }
        }

        val titles = composeRule.onAllNodesWithText(longTitle).fetchSemanticsNodes()
        val lineCounts = titles.map { node ->
            val layouts = mutableListOf<TextLayoutResult>()
            node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
            layouts.single().lineCount
        }
        assertEquals(listOf(1), lineCounts.distinct())
    }

    @Test
    fun `toggles the sorting when the sorting icon is clicked`() {
        showGallery { GalleryState(sorting = GallerySortingState.DATE_DESC) }

        composeRule.onNodeWithContentDescription("Sorting").performClick()

        assertEquals(listOf<GalleryAction>(ChangeGallerySorting(GallerySortingState.DATE_ASC)), actions)
    }

    @Test
    fun `shows the latest title when the state changes`() {
        var state by mutableStateOf(GalleryState(title = Title.Text("Summer")))
        showGallery { state }

        state = GalleryState(title = Title.Text("Winter"))

        composeRule.onNodeWithText("Winter").assertExists()
    }
}
