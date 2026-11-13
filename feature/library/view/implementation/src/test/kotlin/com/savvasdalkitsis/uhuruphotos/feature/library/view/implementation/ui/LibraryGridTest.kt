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
package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.test.core.app.ApplicationProvider
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.TRASH
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState.FoundState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState.RequiresPermissionsState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LibraryGridTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun showLibrary(state: LibraryState) {
        PermissionFlow.init(ApplicationProvider.getApplicationContext())
        composeRule.setContent {
            ComposeResourcesContext()
            SharedTransitionLayout {
                AnimatedContent(targetState = Unit) {
                    CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                        Box(Modifier.size(200.dp, 3000.dp)) {
                            LibraryGrid(
                                contentPadding = PaddingValues(),
                                state = state,
                                action = {},
                            )
                        }
                    }
                }
            }
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
    fun `shows the trash on a library too narrow for two columns`() {
        showLibrary(LibraryState(items = persistentListOf(TRASH, HIDDEN)))

        composeRule.onNodeWithText("Trash").assertIsDisplayed()
        composeRule.onNodeWithText("Hidden media").assertIsDisplayed()
    }

    @Test
    fun `shows every library item on a library too narrow for two columns`() {
        showLibrary(
            LibraryState(
                items = persistentListOf(*LibraryItemState.entries.toTypedArray()),
                autoAlbums = VitrineState(),
                userAlbums = VitrineState(),
                favouritePhotos = VitrineState(),
                localMedia = RequiresPermissionsState(persistentListOf()),
            )
        )

        for (title in listOf("Trash", "Hidden media", "Local albums", "Auto albums")) {
            composeRule.onNodeWithText(title).assertExists()
        }
    }

    @Test
    fun `shows the local folders on a library too narrow for two columns`() {
        showLibrary(
            LibraryState(
                items = persistentListOf(LibraryItemState.LOCAL, TRASH),
                localMedia = FoundState(persistentListOf(), false),
            )
        )

        composeRule.onNodeWithText("Local albums").assertIsDisplayed()
        composeRule.onNodeWithText("Trash").assertIsDisplayed()
    }
}
