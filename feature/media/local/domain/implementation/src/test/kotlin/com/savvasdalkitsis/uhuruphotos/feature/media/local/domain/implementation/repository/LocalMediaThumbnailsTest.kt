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
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.nio.file.Files

class LocalMediaThumbnailsTest {

    private val filesDir = Files.createTempDirectory("files").toFile()
    private val folder = File(filesDir, "localThumbnails").apply { mkdirs() }
    private val context: Context = mockk { every { filesDir } returns this@LocalMediaThumbnailsTest.filesDir }
    private val underTest = LocalMediaThumbnails(context, mockk(), mockk())

    @After
    fun tearDown() {
        filesDir.deleteRecursively()
    }

    private fun thumbnails(vararg names: String) = names.forEach { File(folder, it).writeText("jpg") }

    private fun remaining() = folder.list().orEmpty().sorted()

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
}
