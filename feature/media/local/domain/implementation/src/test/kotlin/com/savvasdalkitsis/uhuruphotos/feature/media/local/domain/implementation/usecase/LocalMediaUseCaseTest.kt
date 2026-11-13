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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaFolderRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.joda.time.format.DateTimeFormat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalMediaUseCaseTest {

    private val cameraBucketId = 540528482
    private val screenshotsBucketId = 5
    private val localMediaRepository: LocalMediaRepository = mockk {
        coEvery { getMedia() } returns listOf(
            details(1, cameraBucketId, "Camera"),
            details(2, cameraBucketId, "camera"),
            details(3, screenshotsBucketId, "Screenshots"),
        )
    }
    private val localMediaFolderRepository: LocalMediaFolderRepository = mockk()
    private val underTest = LocalMediaUseCase(
        context = mockk(),
        localMediaItemMapper = LocalMediaItemMapper(
            localMediaDateTimeFormat = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss"),
            parsingDateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"),
            parsingDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd"),
            dateDisplayer = mockk(relaxed = true),
        ),
        localMediaRepository = localMediaRepository,
        permissionFlow = mockk(),
        localMediaFolderRepository = localMediaFolderRepository,
        mediaStoreVersionRepository = mockk(relaxed = true),
        workerStatusUseCase = mockk(),
        bitmapUseCase = mockk(),
        localMediaWorkScheduler = mockk(),
    )

    @Before
    fun setUp() {
        mockkStatic(ContextCompat::class)
        every { ContextCompat.checkSelfPermission(any(), any()) } returns PERMISSION_GRANTED
    }

    @After
    fun tearDown() {
        unmockkStatic(ContextCompat::class)
    }

    private fun details(id: Long, bucketId: Int, bucketName: String) = LocalMediaItemDetails(
        id = id,
        displayName = "photo$id.jpg",
        dateTaken = "2024/05/06 07:08:09",
        bucketId = bucketId,
        bucketName = bucketName,
        width = 4,
        height = 3,
        size = 12,
        contentUri = "content://media/external/images/media/$id",
        md5 = "md5$id",
        video = false,
        duration = null,
        latLon = null,
        fallbackColor = null,
        path = null,
        orientation = null,
        thumbnailPath = null,
    )

    private suspend fun foundMedia(defaultFolder: Int): LocalMediaItems.Found {
        coEvery { localMediaFolderRepository.getDefaultLocalFolderId() } returns defaultFolder
        return underTest.getLocalMediaItems() as LocalMediaItems.Found
    }

    @Test
    fun `lists a bucket once when its items carry different folder names`() = runTest {
        val media = foundMedia(defaultFolder = screenshotsBucketId)

        assertEquals(
            listOf(LocalMediaFolder(cameraBucketId, "Camera") to listOf(1L, 2L)),
            media.localMediaFolders.map { (folder, items) -> folder to items.map { it.id } },
        )
    }

    @Test
    fun `keeps every item of a default folder with different folder names`() = runTest {
        val media = foundMedia(defaultFolder = cameraBucketId)

        assertEquals(listOf(1L, 2L), media.primaryLocalMediaFolder?.second?.map { it.id })
        assertEquals(listOf(screenshotsBucketId), media.localMediaFolders.map { (folder, _) -> folder.id })
    }
}
