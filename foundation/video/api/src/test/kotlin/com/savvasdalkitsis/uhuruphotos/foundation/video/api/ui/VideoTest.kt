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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.LocalThumbnailImageLoader
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.LocalExoPlayerProvider
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VideoTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val listeners = mutableListOf<Player.Listener>()
    private val mediaItem = slot<MediaItem>()
    private val player = mockk<ExoPlayer>(relaxed = true) {
        every { applicationLooper } returns Looper.getMainLooper()
        every { currentPosition } returns 4_200L
        every { addListener(capture(listeners)) } just Runs
        every { removeListener(any()) } answers { listeners.remove(firstArg()) }
        every { setMediaItem(capture(mediaItem), any<Long>()) } just Runs
        every { currentMediaItem } answers { if (mediaItem.isCaptured) mediaItem.captured else null }
    }
    private val loading = hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
    private var playersCreated = 0
    private val exoplayerProvider = object : ExoplayerProvider {
        @Composable
        override fun createExoplayer(url: String): ExoPlayer {
            DisposableEffect(url) {
                playersCreated++
                onDispose { mediaItem.clear() }
            }
            return player
        }
    }

    @Composable
    private fun PlayingVideo(play: Boolean = true, videoUrl: String = "https://photos.example.com/media/video/1") {
        CompositionLocalProvider(
            LocalExoPlayerProvider provides exoplayerProvider,
            LocalThumbnailImageLoader provides ImageLoader(LocalContext.current),
        ) {
            Video(
                modifier = Modifier,
                videoUrl = videoUrl,
                play = play,
                videoThumbnailUrl = "https://photos.example.com/media/thumbnails/1",
                onFinishedLoading = {},
            )
        }
    }

    @Test
    fun `resumes playback where it was when it is recreated`() {
        val restoration = StateRestorationTester(composeRule)
        restoration.setContent { PlayingVideo() }

        restoration.emulateSavedInstanceStateRestore()

        verify { player.setMediaItem(any(), 4_200L) }
    }

    @Test
    fun `shows only the thumbnail without a player until it should play`() {
        var play by mutableStateOf(false)
        composeRule.setContent { PlayingVideo(play) }
        composeRule.onNode(loading).assertExists()
        assertEquals(0, playersCreated)

        composeRule.runOnIdle { play = true }
        composeRule.waitForIdle()

        assertEquals(1, playersCreated)
        verify(exactly = 1) { player.prepare() }
    }

    @Test
    fun `pauses the player it built once it should stop playing`() {
        var play by mutableStateOf(true)
        composeRule.setContent { PlayingVideo(play) }

        composeRule.runOnIdle { play = false }
        composeRule.waitForIdle()

        verify { player.pause() }
        assertEquals(1, playersCreated)
    }

    @Test
    fun `shows the player instead of the thumbnail once the video is ready`() {
        composeRule.setContent { PlayingVideo() }
        composeRule.onNode(loading).assertExists()

        composeRule.runOnIdle {
            listeners.filterIsInstance<VideoLoadingListener>().single().onPlaybackStateChanged(Player.STATE_READY)
        }

        composeRule.onNode(loading).assertDoesNotExist()
    }

    @Test
    fun `resumes instead of restarting when it plays again`() {
        var play by mutableStateOf(true)
        composeRule.setContent { PlayingVideo(play) }

        composeRule.runOnIdle { play = false }
        composeRule.runOnIdle { play = true }
        composeRule.waitForIdle()

        verify(exactly = 1) { player.setMediaItem(any(), any<Long>()) }
        verify(exactly = 1) { player.prepare() }
        verify(exactly = 2) { player.playWhenReady = true }
        assertEquals(1, listeners.filterIsInstance<VideoLoadingListener>().size)
    }

    @Test
    fun `keeps a single loading listener on the player across videos`() {
        var videoUrl by mutableStateOf("https://photos.example.com/media/video/1")
        composeRule.setContent { PlayingVideo(videoUrl = videoUrl) }

        composeRule.runOnIdle { videoUrl = "https://photos.example.com/media/video/2" }
        composeRule.waitForIdle()

        verify(exactly = 2) { player.setMediaItem(any(), any<Long>()) }
        assertEquals(1, listeners.filterIsInstance<VideoLoadingListener>().size)
    }
}
