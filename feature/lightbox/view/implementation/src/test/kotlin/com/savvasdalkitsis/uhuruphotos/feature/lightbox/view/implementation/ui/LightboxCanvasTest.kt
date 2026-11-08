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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import android.content.Context
import android.os.Looper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.test.core.app.ApplicationProvider
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.LocalServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalFullImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import dev.shreyaspatil.permissionFlow.PermissionFlow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import me.saket.telephoto.zoomable.rememberZoomableState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LightboxCanvasTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val player = mockk<ExoPlayer>(relaxed = true) {
        every { applicationLooper } returns Looper.getMainLooper()
    }
    private val exoplayerProvider = object : ExoplayerProvider {
        @Composable
        override fun createExoplayer(url: String): ExoPlayer = player
    }
    private fun mediaItem(id: String, isVideo: Boolean) = SingleMediaItemState(
        id = RemoteIdModel(id, isVideo, MediaItemHashModel(Md5Hash(id), 1)),
    )
    private val media = persistentListOf(
        mediaItem("photo", isVideo = false),
        mediaItem("video", isVideo = true),
    )

    @Before
    fun initialisePermissions() {
        PermissionFlow.init(ApplicationProvider.getApplicationContext())
    }

    private var recompositions by mutableIntStateOf(0)

    private fun showPage(index: Int, state: LightboxState) {
        composeRule.setContent {
            ComposeResourcesContext()
            CompositionLocalProvider(
                LocalServerUrl provides "https://photos.example.com",
                LocalExoPlayerProvider provides exoplayerProvider,
                LocalThumbnailImageLoader provides remember { ImageLoader(context) },
                LocalFullImageLoader provides remember { ImageLoader(context) },
                LocalNavigator provides remember { mockk(relaxed = true) },
            ) {
                val scrollState = rememberScrollState()
                val zoomableState = rememberZoomableState()
                SharedTransitionLayout {
                    AnimatedContent(targetState = Unit) {
                        CompositionLocalProvider(LocalNavAnimatedContentScope provides this) {
                            Text("recomposed $recompositions times")
                            LightboxCanvas(
                                action = {},
                                state = state,
                                index = index,
                                contentPadding = PaddingValues(),
                                scrollState = scrollState,
                                zoomableState = zoomableState,
                            )
                        }
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun showVideoPage(currentIndex: Int) {
        showPage(index = 1, state = LightboxState(media = media).copyWithIndex(currentIndex))
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ComposeResourcesContext() {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
    }

    @Test
    fun `does not play the video of a neighbouring page`() {
        showVideoPage(currentIndex = 0)

        verify(exactly = 0) { player.prepare() }
    }

    @Test
    fun `plays the video of the current page`() {
        showVideoPage(currentIndex = 1)

        verify { player.prepare() }
    }

    @Test
    fun `keeps playing the video when the lightbox recomposes`() {
        showVideoPage(currentIndex = 1)

        recompositions++
        composeRule.waitForIdle()

        verify(exactly = 1) { player.prepare() }
    }

    @Test
    fun `shows the photo of a photo page`() {
        showPage(index = 0, state = LightboxState(media = media))

        composeRule.onNodeWithContentDescription("Photo").assertExists()
        verify(exactly = 0) { player.prepare() }
    }

    @Test
    fun `shows every dialog the state asks for`() {
        showPage(
            index = 1,
            state = LightboxState(
                media = media,
                showDeleteConfirmationDialog = true,
                showTrashingConfirmationDialog = true,
                showFullySyncedDeleteConfirmationDialog = true,
                showRestorationConfirmationDialog = true,
                showCannotUploadDialog = true,
                showCannotCheckUploadStatusDialog = true,
                showUpsellDialog = true,
            ),
        )

        composeRule.onAllNodes(isDialog()).assertCountEquals(7)
    }
}
