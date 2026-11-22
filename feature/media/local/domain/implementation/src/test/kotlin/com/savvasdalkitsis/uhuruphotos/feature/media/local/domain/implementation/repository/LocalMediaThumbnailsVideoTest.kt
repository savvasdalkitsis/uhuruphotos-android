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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Video
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class LocalMediaThumbnailsVideoTest {

    private val contentUri = Uri.parse("content://media/external/video/media/1")
    private val context: Context = mockk()
    private val underTest = LocalMediaThumbnails(context, mockk(), mockk())

    private fun video(width: Int?, height: Int?) =
        Video(1, "1.mp4", 0, 7, "Camera", width, height, 1, 1_000, contentUri, "", null)

    private val Bitmap.size get() = width to height

    @Before
    fun setUp() {
        mockkConstructor(MediaMetadataRetriever::class)
        every { anyConstructed<MediaMetadataRetriever>().setDataSource(context, contentUri) } just Runs
        every { anyConstructed<MediaMetadataRetriever>().release() } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `grabs a video frame already shrunk to the thumbnail size`() {
        every {
            anyConstructed<MediaMetadataRetriever>().getScaledFrameAtTime(0, OPTION_CLOSEST_SYNC, 400, 400)
        } returns Bitmap.createBitmap(400, 225, ARGB_8888)

        assertEquals(400 to 225, underTest.bitmap(video(width = 3840, height = 2160))!!.size)
        verify(exactly = 0) { anyConstructed<MediaMetadataRetriever>().getFrameAtTime(any(), any()) }
        verify { anyConstructed<MediaMetadataRetriever>().release() }
    }

    @Test
    fun `grabs a full frame when the video size is unknown`() {
        every {
            anyConstructed<MediaMetadataRetriever>().getFrameAtTime(0, OPTION_CLOSEST_SYNC)
        } returns Bitmap.createBitmap(800, 450, ARGB_8888)

        assertEquals(400 to 225, underTest.bitmap(video(width = null, height = null))!!.size)
        verify { anyConstructed<MediaMetadataRetriever>().release() }
    }

    @Test
    fun `lets go of the retriever when the video cannot be read`() {
        every {
            anyConstructed<MediaMetadataRetriever>().setDataSource(context, contentUri)
        } throws IllegalArgumentException("broken")

        assertNull(underTest.bitmap(video(width = 3840, height = 2160)))
        verify { anyConstructed<MediaMetadataRetriever>().release() }
    }
}
