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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.size
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddToAlbumDialogSharedElementTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var showingCel by mutableStateOf(true)

    @Test
    fun `keeps laying out when returning to the dialog showing the cel being left`() {
        showScreens()
        composeRule.mainClock.autoAdvance = false
        showingCel = false
        Snapshot.sendApplyNotifications()
        repeat(5) { composeRule.mainClock.advanceTimeByFrame() }

        composeRule.onNodeWithText(albumTitle).assertExists()
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    private fun showScreens() {
        composeRule.setContent {
            ComposeResourcesContext()
            val context = LocalContext.current
            CompositionLocalProvider(
                LocalThumbnailImageLoader provides remember { ImageLoader(context) },
                LocalWindowSize provides WindowSizeClass.calculateFromSize(DpSize(400.dp, 800.dp)),
            ) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = showingCel,
                        transitionSpec = { fadeIn(tween(1000)) togetherWith fadeOut(tween(1000)) },
                    ) { cel ->
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            when {
                                cel -> Cel(modifier = Modifier.size(100.dp), state = cover, onSelected = {})
                                else -> AddToAlbumDialog(persistentListOf(album)) {}
                            }
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

    private companion object {
        const val albumTitle = "Holidays"
        val hash = MediaItemHashModel.fromRemoteMediaHash("hash", 1)
        val cover = CelState(MediaItemInstanceModel(MediaIdModel.RemoteIdModel("id", false, hash), hash))
        val album = UserAlbumState(
            id = 1,
            cover = VitrineState(cel1 = cover),
            title = Title.Text(albumTitle),
            photoCount = 1,
        )
    }
}
