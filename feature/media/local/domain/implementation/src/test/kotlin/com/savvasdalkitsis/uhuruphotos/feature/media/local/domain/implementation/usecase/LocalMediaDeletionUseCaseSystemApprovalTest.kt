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

import android.app.Application
import android.os.Build.VERSION_CODES.BAKLAVA
import android.provider.MediaStore
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequestModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestLauncher
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [BAKLAVA])
class LocalMediaDeletionUseCaseSystemApprovalTest {

    private val context: Application = RuntimeEnvironment.getApplication()
    private val mediaStore = Robolectric.setupContentProvider(FakeMediaStoreProvider::class.java, MediaStore.AUTHORITY)
    private val localMediaService = LocalMediaService(context, context.contentResolver)
    private val localMediaRepository: LocalMediaRepository = mockk(relaxed = true)
    private var approvals = 0
    private val systemDeletesOnApproval = ActivityRequestLauncher { _, _ ->
        approvals++
        mediaStore.existingIds.clear()
        Ok(Unit)
    }
    private val underTest = LocalMediaDeletionUseCase(
        context, localMediaRepository, localMediaService, context.contentResolver, systemDeletesOnApproval,
    )

    private suspend fun delete(vararg ids: Long) =
        underTest.deleteLocalMediaItems(ids.map { LocalMediaDeletionRequestModel(it, false) })

    @Test
    fun `does not ask the media store again once the user approved the deletion`() = runTest {
        mediaStore.existingIds += 1

        val result = delete(1)

        assertEquals(LocalMediaItemDeletion.Success, result)
        assertEquals(listOf(listOf(localMediaService.createMediaItemUri(1, false))), mediaStore.deleteRequests)
        verify { localMediaRepository.removeItemsFromDb(1) }
    }

    @Test
    fun `treats media already gone from the media store as deleted`() = runTest {
        val result = delete(1)

        assertEquals(LocalMediaItemDeletion.Success, result)
        assertEquals(0, approvals)
        verify { localMediaRepository.removeItemsFromDb(1) }
    }

    @Test
    fun `asks approval only for media still in the media store`() = runTest {
        mediaStore.existingIds += 2

        val result = delete(1, 2)

        assertEquals(LocalMediaItemDeletion.Success, result)
        assertEquals(listOf(listOf(localMediaService.createMediaItemUri(2, false))), mediaStore.deleteRequests)
    }

    @Test
    fun `reports an error when the media store rejects the delete request`() = runTest {
        mediaStore.existingIds += 1
        mediaStore.rejectedIds += 1

        val result = delete(1)

        assertTrue(result is LocalMediaItemDeletion.Error)
        assertEquals(0, approvals)
    }
}
