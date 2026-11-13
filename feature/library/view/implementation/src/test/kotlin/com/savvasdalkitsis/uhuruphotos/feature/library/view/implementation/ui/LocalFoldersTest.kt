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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMediaState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalFoldersTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val cameraBucketId = 540528482

    private fun showFolders(folders: List<LocalMediaFolder>, width: Dp) {
        composeRule.setContent {
            ComposeResourcesContext()
            SharedTransitionLayout {
                AnimatedContent(targetState = Unit) {
                    CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                        Box(Modifier.width(width)) {
                            LocalFolders(
                                title = "Folders",
                                media = LibraryLocalMediaState.FoundState(
                                    buckets = folders.map { it to VitrineState() }.toImmutableList(),
                                    scanningOtherFolders = true,
                                ),
                                action = {},
                            )
                        }
                    }
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
    fun `shows a bucket only once when its items carry different folder names`() {
        showFolders(
            listOf(
                LocalMediaFolder(cameraBucketId, "Camera"),
                LocalMediaFolder(2, "Screenshots"),
                LocalMediaFolder(cameraBucketId, "camera"),
            ),
            width = 800.dp,
        )

        composeRule.onAllNodesWithText("Camera").assertCountEquals(1)
        composeRule.onAllNodesWithText("camera").assertCountEquals(0)
    }

    @Test
    fun `flings through folders that share a bucket id`() {
        val folders = (1..4).map { LocalMediaFolder(it, "Folder $it") } +
            LocalMediaFolder(cameraBucketId, "Camera") +
            LocalMediaFolder(cameraBucketId, "camera") +
            LocalMediaFolder(6, "Folder 6")
        showFolders(folders, width = 300.dp)

        repeat(3) { composeRule.onNode(hasScrollAction()).performTouchInput { swipeLeft() } }

        composeRule.onNodeWithText("Folder 6").assertIsDisplayed()
    }
}
