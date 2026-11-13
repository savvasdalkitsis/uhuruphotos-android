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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.test.core.app.ApplicationProvider
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.ItemOrderChanged
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.AUTO
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.HIDDEN
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.LOCAL
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryItemState.TRASH
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState.FoundState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState.RequiresPermissionsState
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryState
import dev.shreyaspatil.permissionFlow.PermissionFlow
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LibraryGridReorderTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actions = mutableListOf<LibraryAction>()

    private fun showLibrary(state: LibraryState) {
        PermissionFlow.init(ApplicationProvider.getApplicationContext())
        composeRule.setContent {
            ComposeResourcesContext()
            SharedTransitionLayout {
                AnimatedContent(targetState = Unit) {
                    CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                        Box(Modifier.size(200.dp, 600.dp)) {
                            LibraryGrid(
                                contentPadding = PaddingValues(),
                                state = state,
                                action = { actions += it },
                            )
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun dragDown(title: String) {
        composeRule.onNodeWithText(title).performTouchInput {
            down(center)
            repeat(40) { moveBy(Offset(0f, 25f)) }
            up()
        }
        composeRule.waitForIdle()
    }

    private val lastOrder get() = actions.filterIsInstance<ItemOrderChanged>().lastOrNull()?.items

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    private fun items(vararg items: LibraryItemState) = persistentListOf(*items)

    @Test
    fun `drags a library item below the next one`() {
        showLibrary(LibraryState(items = items(TRASH, HIDDEN)))

        dragDown("Trash")

        assertEquals(listOf(HIDDEN, TRASH), lastOrder)
    }

    @Test
    fun `drags a library item past items that are not shown`() {
        showLibrary(LibraryState(items = items(AUTO, TRASH, HIDDEN)))

        dragDown("Trash")

        assertEquals(listOf(AUTO, HIDDEN, TRASH), lastOrder)
    }

    @Test
    fun `drags a library item below an album shelf`() {
        showLibrary(LibraryState(items = items(TRASH, AUTO), autoAlbums = VitrineState()))

        dragDown("Trash")

        assertEquals(listOf(AUTO, TRASH), lastOrder)
    }

    @Test
    fun `drags the local folders below the next item`() {
        showLibrary(LibraryState(items = items(LOCAL, TRASH), localMedia = FoundState(persistentListOf(), false)))

        dragDown("Local albums")

        assertEquals(listOf(TRASH, LOCAL), lastOrder)
    }

    @Test
    fun `drags the local folders asking for permissions below the next item`() {
        showLibrary(LibraryState(items = items(LOCAL, TRASH), localMedia = RequiresPermissionsState(persistentListOf())))

        dragDown("Local albums")

        assertEquals(listOf(TRASH, LOCAL), lastOrder)
    }
}
