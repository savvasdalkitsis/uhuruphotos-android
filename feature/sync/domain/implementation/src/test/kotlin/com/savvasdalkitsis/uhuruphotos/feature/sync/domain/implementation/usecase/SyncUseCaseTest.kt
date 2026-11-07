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
package com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.model.SyncStatus
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SyncUseCaseTest {

    private val hash = MediaItemHashModel(Md5Hash("md5"), 1)
    private val otherHash = MediaItemHashModel(Md5Hash("other"), 1)
    private val local = MediaIdModel.LocalIdModel(1, 0, false, "content://1", "", hash)
    private val remote = MediaIdModel.RemoteIdModel("uuid", false, otherHash)
    private val sameDay = collection("day", MediaItemInstanceModel(local, hash), MediaItemInstanceModel(remote, otherHash))

    private fun collection(id: String, vararg items: MediaItemInstanceModel) =
        MediaCollectionModel(id = id, mediaItems = items.toList(), displayTitle = id)

    private fun feed(vararg collections: MediaCollectionModel): FeedUseCase = mockk {
        every { observeFeed(any(), any()) } returns flowOf(collections.toList())
    }
    private val queuedJob = UploadJob(localItemId = 2, displayName = null, contentUri = "content://2")

    private fun underTest(
        status: SyncStatus,
        jobs: List<UploadJob> = listOf(queuedJob),
        feedUseCase: FeedUseCase = feed(sameDay),
    ) = spyk(SyncUseCase(mockk(), mockk(), feedUseCase, mockk(), uploadsUseCase(jobs))) {
            every { observeSyncStatus() } returns flowOf(status)
        }

    private fun uploadsUseCase(jobs: List<UploadJob>): UploadsUseCase = mockk {
        every { observeUploadsInFlight() } returns flowOf(Uploads(jobs = jobs))
    }

    @Test
    fun `queued uploads drain while cloud sync is off`() = runTest {
        assertEquals(
            listOf(UploadItem(2, "content://2")),
            underTest(SyncStatus.Disabled).observePendingItems().first(),
        )
    }

    @Test
    fun `cloud sync adds local only items to the queued ones`() = runTest {
        assertEquals(
            listOf(UploadItem(1, "content://1"), UploadItem(2, "content://2")),
            underTest(SyncStatus.Enabled).observePendingItems().first(),
        )
    }

    @Test
    fun `items being processed are not uploaded again`() = runTest {
        val processing = UploadJob(3, null, "content://3", UploadStatus.Processing)
        val failed = UploadJob(4, null, "content://4", UploadStatus.Failed(null))

        assertEquals(
            listOf(UploadItem(2, "content://2")),
            underTest(SyncStatus.Disabled, listOf(queuedJob, processing, failed)).observePendingItems().first(),
        )
    }

    @Test
    fun `cloud sync skips local only items that are already being processed`() = runTest {
        val processing = UploadJob(1, null, "content://1", UploadStatus.Processing)

        assertEquals(
            emptyList<UploadItem>(),
            underTest(SyncStatus.Enabled, listOf(processing)).observePendingItems().first(),
        )
    }

    @Test
    fun `cloud sync skips local items whose copy on the server is on another day`() = runTest {
        val undated = collection("undated", MediaItemInstanceModel(MediaIdModel.RemoteIdModel("uuid", false, hash), hash))

        assertEquals(
            emptyList<UploadItem>(),
            underTest(SyncStatus.Enabled, emptyList(), feed(sameDay, undated)).observePendingItems().first(),
        )
    }
}
