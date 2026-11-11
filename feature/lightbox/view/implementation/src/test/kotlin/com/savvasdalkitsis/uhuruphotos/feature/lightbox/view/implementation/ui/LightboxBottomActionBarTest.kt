/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LightboxBottomActionBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sharedLauncherIcon = 0x7F0F0000

    private fun editApp(packageName: String, activity: String) = ResolveInfo().apply {
        nonLocalizedLabel = "$packageName/$activity"
        activityInfo = ActivityInfo().apply {
            this.packageName = packageName
            name = activity
            applicationInfo = ApplicationInfo().apply {
                this.packageName = packageName
                icon = sharedLauncherIcon
            }
        }
    }

    private fun showEditApps(apps: List<ResolveInfo>, width: Dp = 200.dp) {
        composeRule.setContent {
            ComposeResourcesContext()
            Box(Modifier.width(width)) {
                LightboxBottomActionBar(
                    mediaItem = SingleMediaItemState(
                        id = RemoteIdModel("photo", false, MediaItemHashModel(Md5Hash("photo"), 1)),
                        showEditApps = apps.toImmutableList(),
                    ),
                    showRestoreButton = false,
                    action = {},
                )
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

    private fun scrollTo(label: String) {
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(label))
    }

    @Test
    fun `scrolls through edit apps that share the same icon resource`() {
        val apps = (1..8).map { editApp("com.editor$it", "EditActivity") }
        showEditApps(apps)

        scrollTo("com.editor8/EditActivity")

        composeRule.onNodeWithText("com.editor8/EditActivity").assertIsDisplayed()
    }

    @Test
    fun `scrolls through several edit activities of the same app`() {
        val apps = listOf("Markup", "Editor", "Collage", "Crop", "Filters").map { editApp("com.gallery", it) }
        showEditApps(apps)

        scrollTo("com.gallery/Filters")

        composeRule.onNodeWithText("com.gallery/Filters").assertIsDisplayed()
    }

    @Test
    fun `shows an edit activity only once when it is resolved twice`() {
        val apps = listOf(editApp("com.editor", "EditActivity"), editApp("com.editor", "EditActivity"))
        showEditApps(apps, width = 600.dp)

        composeRule.onAllNodesWithText("com.editor/EditActivity").assertCountEquals(1)
    }
}
