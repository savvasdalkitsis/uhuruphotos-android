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
package com.savvasdalkitsis.uhuruphotos.foundation.exif.implementation.usecase

import android.util.Log
import androidx.exifinterface.media.ExifInterface
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExifUseCaseTest {

    private val underTest = ExifUseCase()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.isLoggable(any(), any()) } returns false
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `prefers the original date over the modification date`() {
        val exif = exif(original = 1L, modified = 2L, digitized = 3L)

        assertEquals(1L, with(underTest) { exif.metadata() }.dateTime)
    }

    @Test
    fun `uses the modification date when there is no original date`() {
        val exif = exif(original = null, modified = 2L, digitized = 3L)

        assertEquals(2L, with(underTest) { exif.metadata() }.dateTime)
    }

    @Test
    fun `falls back to the digitized date`() {
        val exif = exif(original = null, modified = null, digitized = 3L)

        assertEquals(3L, with(underTest) { exif.metadata() }.dateTime)
    }

    private fun exif(original: Long?, modified: Long?, digitized: Long?) =
        mockk<ExifInterface>(relaxed = true) {
            every { latLong } returns null
            every { dateTimeOriginal } returns original
            every { dateTime } returns modified
            every { dateTimeDigitized } returns digitized
        }
}
