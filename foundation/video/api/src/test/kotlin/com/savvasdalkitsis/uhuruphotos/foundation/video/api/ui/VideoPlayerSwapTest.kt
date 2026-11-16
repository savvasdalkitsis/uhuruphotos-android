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
package com.savvasdalkitsis.uhuruphotos.foundation.video.api.ui

import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VideoPlayerSwapTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val listeners = mutableMapOf<ExoPlayer, MutableList<Player.Listener>>()
    private val firstPlayer = player()
    private val secondPlayer = player()
    private var video by mutableStateOf(firstPlayer to "https://photos.example.com/media/video/1")
    private lateinit var root: View

    @Test
    fun `shows the player of the video it currently plays`() {
        composeRule.setContent {
            root = LocalView.current
            CompositionLocalProvider(LocalThumbnailImageLoader provides ImageLoader(LocalContext.current)) {
                Video(
                    modifier = Modifier,
                    exoPlayer = video.first,
                    videoUrl = video.second,
                    play = true,
                    videoThumbnailUrl = "https://photos.example.com/media/thumbnails/1",
                    onFinishedLoading = {},
                )
            }
        }
        becomesReady(firstPlayer)

        video = secondPlayer to "https://photos.example.com/media/video/2"
        composeRule.waitForIdle()
        becomesReady(secondPlayer)

        assertSame(secondPlayer, root.playerViews().single().player)
    }

    private fun player(): ExoPlayer = mockk(relaxed = true) {
        every { applicationLooper } returns Looper.getMainLooper()
        every { addListener(any()) } answers { listeners.getOrPut(self as ExoPlayer) { mutableListOf() } += firstArg<Player.Listener>() }
    }

    private fun becomesReady(player: ExoPlayer) {
        composeRule.runOnIdle {
            listeners.getValue(player).forEach { it.onPlaybackStateChanged(Player.STATE_READY) }
        }
        composeRule.waitForIdle()
    }

    private fun View.playerViews(): List<PlayerView> = when (this) {
        is PlayerView -> listOf(this)
        is ViewGroup -> (0 until childCount).flatMap { getChildAt(it).playerViews() }
        else -> emptyList()
    }
}
