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

import android.content.Context
import android.os.Looper
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayerViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val player = mockk<ExoPlayer>(relaxed = true) {
        every { applicationLooper } returns Looper.getMainLooper()
    }

    @Test
    fun `mutes the player and crops the video when asked to`() {
        val view = playerView(context, player, showControls = false, mute = true, crop = true)

        verify { player.volume = 0f }
        assertEquals(AspectRatioFrameLayout.RESIZE_MODE_ZOOM, view.resizeMode)
        assertFalse(view.useController)
    }

    @Test
    fun `keeps the sound and the whole video by default`() {
        val view = playerView(context, player, showControls = true, mute = false, crop = false)

        verify(exactly = 0) { player.volume = any() }
        assertEquals(AspectRatioFrameLayout.RESIZE_MODE_FIT, view.resizeMode)
        assertTrue(view.useController)
    }
}
