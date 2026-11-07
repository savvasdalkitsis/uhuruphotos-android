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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequestModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LocalMediaDeletionUseCaseTest {

    private val localMediaRepository: LocalMediaRepository = mockk {
        every { deletePhotos(*anyLongVararg()) } returns Ok(Unit)
        every { deleteVideos(*anyLongVararg()) } returns Ok(Unit)
    }
    private val underTest = LocalMediaDeletionUseCase(mockk(), localMediaRepository, mockk(), mockk(), mockk())

    @Before
    fun setUp() {
        mockkStatic(ContextCompat::class)
        every { ContextCompat.checkSelfPermission(any(), any()) } returns PERMISSION_GRANTED
    }

    @After
    fun tearDown() {
        unmockkStatic(ContextCompat::class)
    }

    @Test
    fun `deleting only photos does not try to delete videos`() = runTest {
        val result = underTest.deleteLocalMediaItems(listOf(LocalMediaDeletionRequestModel(1, false)))

        assertEquals(LocalMediaItemDeletion.Success, result)
        verify { localMediaRepository.deletePhotos(1) }
        verify(exactly = 0) { localMediaRepository.deleteVideos(*anyLongVararg()) }
    }

    @Test
    fun `deleting only videos does not try to delete photos`() = runTest {
        val result = underTest.deleteLocalMediaItems(listOf(LocalMediaDeletionRequestModel(2, true)))

        assertEquals(LocalMediaItemDeletion.Success, result)
        verify { localMediaRepository.deleteVideos(2) }
        verify(exactly = 0) { localMediaRepository.deletePhotos(*anyLongVararg()) }
    }
}
