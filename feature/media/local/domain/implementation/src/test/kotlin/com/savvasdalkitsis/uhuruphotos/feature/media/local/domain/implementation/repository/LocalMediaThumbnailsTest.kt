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

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
import androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.BitmapUseCase
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files

class LocalMediaThumbnailsTest {

    private val filesDir = Files.createTempDirectory("files").toFile()
    private val folder = File(filesDir, "localThumbnails").apply { mkdirs() }
    private val context: Context = mockk { every { filesDir } returns this@LocalMediaThumbnailsTest.filesDir }
    private val contentUri: Uri = mockk()
    private val contentResolver: ContentResolver = mockk {
        every { openInputStream(contentUri) } answers { ByteArrayInputStream(ByteArray(0)) }
    }
    private val bitmapUseCase: BitmapUseCase = mockk()
    private val underTest = LocalMediaThumbnails(context, contentResolver, bitmapUseCase)
    private val decoded: Bitmap = bitmap(width = 800, height = 600)
    private val scaled: Bitmap = bitmap(width = 400, height = 300)
    private val rotated: Bitmap = bitmap(width = 300, height = 400)

    @Before
    fun setUp() {
        mockkStatic(Log::class, BitmapFactory::class, Bitmap::class)
        every { Log.isLoggable(any(), any()) } returns false
        Class.forName(ExifInterface::class.java.name)
        every { BitmapFactory.decodeStream(any()) } returns decoded
        every { Bitmap.createScaledBitmap(decoded, 400, 300, true) } returns scaled
        every { Bitmap.createBitmap(scaled, 0, 0, 400, 300, any(), true) } returns rotated
        mockkConstructor(Matrix::class)
        every { anyConstructed<Matrix>().postRotate(any()) } returns true
    }

    @After
    fun tearDown() {
        unmockkAll()
        filesDir.deleteRecursively()
    }

    private fun bitmap(width: Int, height: Int): Bitmap = mockk(relaxed = true) {
        every { this@mockk.width } returns width
        every { this@mockk.height } returns height
    }

    private fun photo(orientation: String?) =
        Photo(1, "1.jpg", 0, 7, "Camera", 800, 600, 1, contentUri, "", orientation)

    private fun thumbnails(vararg names: String) = names.forEach { File(folder, it).writeText("jpg") }

    private fun remaining() = folder.list().orEmpty().sorted()

    @Test
    fun `rotates photos to their media store orientation`() {
        assertSame(rotated, underTest.bitmap(photo(orientation = "90")))
        verify { anyConstructed<Matrix>().postRotate(90f) }
    }

    @Test
    fun `keeps photos without an orientation as they are`() {
        assertSame(scaled, underTest.bitmap(photo(orientation = null)))
        assertSame(scaled, underTest.bitmap(photo(orientation = "0")))
    }

    @Test
    fun `saves rotated thumbnails without the orientation of the original`() {
        mockkConstructor(ExifInterface::class)
        every { anyConstructed<ExifInterface>().setAttribute(any(), any()) } just Runs
        every { anyConstructed<ExifInterface>().saveAttributes() } just Runs
        every { with(bitmapUseCase) { rotated.toJpeg() } } returns EMPTY_JPEG
        every { with(bitmapUseCase) { EMPTY_JPEG.copyExifFrom(contentUri) } } returns EMPTY_JPEG

        val path = underTest.save(rotated, photo(orientation = "90"))

        assertEquals(File(folder, "1.jpg").absolutePath, path)
        verifyOrder {
            anyConstructed<ExifInterface>().setAttribute(TAG_ORIENTATION, ORIENTATION_NORMAL.toString())
            anyConstructed<ExifInterface>().saveAttributes()
        }
    }

    @Test
    fun `deletes the thumbnails of removed media`() {
        thumbnails("1.jpg", "2.jpg", "3.jpg")

        underTest.delete(1, 3)

        assertEquals(listOf("2.jpg"), remaining())
    }

    @Test
    fun `deletes thumbnails left behind by media that is gone`() {
        thumbnails("1.jpg", "2.jpg", "3.jpg", "notes.txt")

        underTest.deleteAllExcept(setOf(2))

        assertEquals(listOf("2.jpg"), remaining())
    }

    @Test
    fun `copes with no thumbnails folder`() {
        folder.deleteRecursively()

        underTest.deleteAllExcept(emptySet())
        underTest.delete(1)

        assertEquals(emptyList<String>(), remaining())
    }

    private companion object {
        val EMPTY_JPEG = listOf(0xFF, 0xD8, 0xFF, 0xD9).map { it.toByte() }.toByteArray()
    }
}
