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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Video
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.MediaStoreContentUriResolver
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalMediaServiceTest {

    private val underTest = LocalMediaService(mockk(), mockk())

    @Before
    fun setUp() {
        mockkObject(MediaStoreContentUriResolver)
        every { MediaStoreContentUriResolver.getContentUriForItem(any(), any()) } returns mockk<Uri>()
    }

    @After
    fun tearDown() {
        unmockkObject(MediaStoreContentUriResolver)
    }

    private fun videoRow(dateTaken: Long, dateAddedInSeconds: Long): Cursor = mockk(relaxed = true) {
        every { getColumnIndexOrThrow(Video.Media.DATE_TAKEN) } returns DATE_TAKEN
        every { getColumnIndexOrThrow(Video.Media.DATE_ADDED) } returns DATE_ADDED
        every { getLong(DATE_TAKEN) } returns dateTaken
        every { getLong(DATE_ADDED) } returns dateAddedInSeconds
    }

    @Test
    fun `uses the date a video was taken`() {
        val video = underTest.videoRowHandler(videoRow(dateTaken = 1_700_000_123_000, dateAddedInSeconds = 1))

        assertEquals(1_700_000_123_000, video.dateTaken)
    }

    @Test
    fun `falls back to the added date of a video in milliseconds`() {
        val video = underTest.videoRowHandler(videoRow(dateTaken = 0, dateAddedInSeconds = 1_700_000_000))

        assertEquals(1_700_000_000_000, video.dateTaken)
    }

    private companion object {
        const val DATE_TAKEN = 1
        const val DATE_ADDED = 2
    }
}
