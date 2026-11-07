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

import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.http.response.UploadCompleteResponseData
import com.squareup.moshi.Moshi
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChunkedUploaderTest {

    private val underTest = ChunkedUploader(mockk(), mockk(), mockk())
    private val completeAdapter = Moshi.Builder().build().adapter(UploadCompleteResponseData::class.java)

    @Test
    fun `content range total is the file size`() {
        assertEquals("bytes 0-999999/2500000", underTest.contentRange(0, 1_000_000, 2_500_000))
        assertEquals("bytes 2000000-2499999/2500000", underTest.contentRange(2_000_000, 500_000, 2_500_000))
    }

    @Test
    fun `content range total never ends before the chunk`() {
        assertEquals("bytes 1000-1999/2000", underTest.contentRange(1_000, 1_000, 0))
    }

    @Test
    fun `reads the photo id of a completed upload`() {
        val response = completeAdapter.fromJson("""{"job_id": "j1", "photo_id": "0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e"}""")!!

        assertEquals("0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e", response.photoId)
    }

    @Test
    fun `reads a duplicate upload without photo id`() {
        val response = completeAdapter.fromJson("""{"detail": "Photo duplicated. No new import performed.", "photo_id": null}""")!!

        assertNull(response.photoId)
    }
}
