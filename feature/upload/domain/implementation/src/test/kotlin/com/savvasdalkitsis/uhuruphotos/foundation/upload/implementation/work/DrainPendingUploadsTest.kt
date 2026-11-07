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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.work

import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DrainPendingUploadsTest {

    private val first = UploadItem(1, "content://1")
    private val second = UploadItem(2, "content://2")
    private val third = UploadItem(3, "content://3")

    @Test
    fun `returns once nothing is pending`() = runTest {
        val uploaded = mutableListOf<UploadItem>()

        val failed = drainPendingUploads(
            pendingItems = { emptyList() },
            markAsUploading = {},
            upload = { item, _, _ -> uploaded += item; true },
        )

        assertEquals(emptySet<UploadItem>(), failed)
        assertEquals(emptyList<UploadItem>(), uploaded)
    }

    @Test
    fun `uploads every item once even while it stays pending`() = runTest {
        val uploaded = mutableListOf<Triple<UploadItem, Int, Int>>()

        val failed = drainPendingUploads(
            pendingItems = { listOf(first, second) },
            markAsUploading = {},
            upload = { item, position, total -> uploaded += Triple(item, position, total); true },
        )

        assertEquals(emptySet<UploadItem>(), failed)
        assertEquals(listOf(Triple(first, 1, 2), Triple(second, 2, 2)), uploaded)
    }

    @Test
    fun `keeps uploading the rest of the queue after a failure`() = runTest {
        val uploaded = mutableListOf<UploadItem>()

        val failed = drainPendingUploads(
            pendingItems = { listOf(first, second) },
            markAsUploading = {},
            upload = { item, _, _ -> uploaded += item; item != first },
        )

        assertEquals(setOf(first), failed)
        assertEquals(listOf(first, second), uploaded)
    }

    @Test
    fun `picks up items queued while uploading`() = runTest {
        val snapshots = ArrayDeque(listOf(listOf(first), listOf(first, second, third)))
        val marked = mutableListOf<List<UploadItem>>()
        val uploaded = mutableListOf<UploadItem>()

        drainPendingUploads(
            pendingItems = { snapshots.removeFirstOrNull() ?: listOf(first, second, third) },
            markAsUploading = { marked += it },
            upload = { item, _, _ -> uploaded += item; true },
        )

        assertEquals(listOf(listOf(first), listOf(second, third)), marked)
        assertEquals(listOf(first, second, third), uploaded)
    }
}
