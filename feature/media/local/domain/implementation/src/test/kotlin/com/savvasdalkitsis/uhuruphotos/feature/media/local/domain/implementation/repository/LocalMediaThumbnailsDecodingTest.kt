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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.graphics.Bitmap.Config.ARGB_8888
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LocalMediaThumbnailsDecodingTest {

    private val contentUri = Uri.parse("content://media/external/images/media/1")
    private var image = ByteArray(0)
    private val contentResolver: ContentResolver = mockk {
        every { openInputStream(contentUri) } answers { ByteArrayInputStream(image) }
    }
    private val underTest = LocalMediaThumbnails(mockk(), contentResolver, mockk())

    private fun thumbnailOf(width: Int, height: Int): Bitmap {
        image = ByteArrayOutputStream().also {
            Bitmap.createBitmap(width, height, ARGB_8888).compress(PNG, 100, it)
        }.toByteArray()
        return underTest.bitmap(Photo(1, "1.png", 0, 7, "Camera", width, height, 1, contentUri, "", null))!!
    }

    private val Bitmap.size get() = width to height

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `shrinks a wide photo to the thumbnail width`() {
        assertEquals(400 to 300, thumbnailOf(width = 800, height = 600).size)
    }

    @Test
    fun `never upscales a photo narrower than a thumbnail`() {
        assertEquals(64 to 4000, thumbnailOf(width = 64, height = 4000).size)
    }

    @Test
    fun `fits a very tall photo within the tallest thumbnail`() {
        assertEquals(320 to 8000, thumbnailOf(width = 420, height = 10500).size)
    }

    @Test
    fun `fits a very wide photo to at least a pixel tall`() {
        assertEquals(400 to 1, thumbnailOf(width = 4000, height = 2).size)
    }

    @Test
    fun `decodes a large photo subsampled instead of at full resolution`() {
        mockkStatic(Bitmap::class)

        assertEquals(400 to 300, thumbnailOf(width = 4000, height = 3000).size)
        verify { Bitmap.createScaledBitmap(match { it.size == 500 to 375 }, 400, 300, true) }
    }
}
