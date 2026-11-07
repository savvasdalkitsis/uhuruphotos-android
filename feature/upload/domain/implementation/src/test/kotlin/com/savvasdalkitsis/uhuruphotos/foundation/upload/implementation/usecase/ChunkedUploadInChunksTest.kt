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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.usecase

import android.content.ContentResolver
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.http.UploadService
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.http.response.UploadChunkResponseData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class ChunkedUploadInChunksTest {

    private val size = 2_500_000
    private val item = UploadItem(1, "content://media/1")
    private val user: User = mockk { every { id } returns 7 }
    private val ranges = mutableListOf<String>()
    private val progress = mutableListOf<Pair<Long, Long>>()
    private val uploadRepository: UploadRepository = mockk(relaxed = true) {
        every { getUploadId(1) } returns "upload"
    }
    private val contentResolver: ContentResolver = mockk {
        every { openInputStream(any()) } answers { ByteArrayInputStream(ByteArray(size)) }
    }
    private val uploadService: UploadService = mockk {
        coEvery { uploadChunk(capture(ranges), any(), any(), any(), any(), any()) } returns
            UploadChunkResponseData("upload")
    }
    private val underTest = ChunkedUploader(uploadRepository, contentResolver, uploadService)

    @Before
    fun setUp() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
    }

    @After
    fun tearDown() {
        unmockkStatic(Uri::class)
    }

    @Test
    fun `every chunk states the file size as the total`() = runTest {
        coEvery { uploadRepository.getOffset(1) } returns null

        val result = underTest.uploadInChunks(item, size, user) { current, total -> progress += current to total }

        assertTrue(result.isOk)
        assertEquals(
            listOf(
                "bytes 0-999999/2500000",
                "bytes 1000000-1999999/2500000",
                "bytes 2000000-2499999/2500000",
            ),
            ranges,
        )
        assertEquals(2_500_000L to 2_500_000L, progress.last())
    }

    @Test
    fun `a resumed upload continues from the stored offset`() = runTest {
        coEvery { uploadRepository.getOffset(1) } returns 2_000_000

        underTest.uploadInChunks(item, size, user) { _, _ -> }

        assertEquals(listOf("bytes 2000000-2499999/2500000"), ranges)
    }

    @Test
    fun `a file of unknown size uses the stream length`() = runTest {
        coEvery { uploadRepository.getOffset(1) } returns null

        underTest.uploadInChunks(item, 0, user) { _, _ -> }

        assertEquals("bytes 2000000-2499999/2500000", ranges.last())
    }
}
