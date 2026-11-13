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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.Processing
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.model.ProcessingItem
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.usecase.ProcessingUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.InQueue
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Processing as ProcessingStatus
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UploadsUseCaseTest {

    private val localMediaUseCase: LocalMediaUseCase = mockk {
        coEvery { getLocalMediaItem(any()) } answers { localItem(firstArg()) }
    }

    private fun localItem(id: Long): LocalMediaItem = mockk {
        every { displayName } returns "$id.jpg"
        every { contentUri } returns "content://$id"
    }

    private fun processing(id: Long, error: String? = null) =
        ProcessingItem(id, "$id.jpg", "content://$id", error = error, md5 = Md5Hash("$id"))

    private fun uploads(uploading: Set<Long>, vararg processing: ProcessingItem) = UploadsUseCase(
        localMediaUseCase = localMediaUseCase,
        uploadUseCase = mockk<UploadUseCase> {
            every { observeUploading() } returns flowOf(uploading)
            every { observeCurrentUpload() } returns flowOf(null)
        },
        processingUseCase = mockk<ProcessingUseCase> {
            every { observeProcessingMedia() } returns flowOf(Processing(processing.toList()))
        },
        context = mockk(),
    ).observeUploadsInFlight()

    @Test
    fun `lists an item uploaded again after it failed processing once as queued`() = runTest {
        val jobs = uploads(setOf(1, 2), processing(1, error = "boom"), processing(3)).first().jobs

        assertEquals(listOf(3L to ProcessingStatus, 1L to InQueue, 2L to InQueue), jobs.map { it.localItemId to it.status })
    }
}
