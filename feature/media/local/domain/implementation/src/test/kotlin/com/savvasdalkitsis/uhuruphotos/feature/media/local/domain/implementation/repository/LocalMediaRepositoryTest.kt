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

import android.net.Uri
import app.cash.sqldelight.Query
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LocalMediaRepositoryTest {

    private val queries: LocalMediaItemDetailsQueries = mockk {
        every { getExistingIds() } returns ids(1)
        every { getExistingBucketIds(7) } returns ids(1)
    }
    private val service: LocalMediaService = mockk {
        coEvery { getPhotosForBucket(7) } returns listOf(photo(1))
        coEvery { getVideosForBucket(7) } returns emptyList()
    }
    private val folders: LocalMediaFolderRepository = mockk {
        coEvery { getDefaultLocalFolderId() } returns 7
        every { shouldScanOtherFolders() } returns false
    }
    private val thumbnails: LocalMediaThumbnails = mockk(relaxed = true)
    private val underTest = LocalMediaRepository(
        mockk(), queries, mockk(), service, folders, mockk(), mockk(), mockk(), thumbnails, mockk(),
    )

    private fun ids(vararg ids: Long): Query<Long> = mockk {
        every { executeAsList() } returns ids.toList()
    }

    private fun photo(id: Long) = Photo(id, "$id.jpg", 0, 7, "Camera", 1, 1, 1, mockk<Uri>(), "", null)

    @Test
    fun `a full scan deletes the thumbnails of media that is not on the device any more`() = runTest {
        underTest.refresh()

        verify { thumbnails.deleteAllExcept(setOf(1)) }
    }

    @Test
    fun `scanning a single folder keeps the thumbnails of other folders`() = runTest {
        underTest.refreshFolder(7)

        verify(exactly = 0) { thumbnails.deleteAllExcept(any()) }
    }
}
