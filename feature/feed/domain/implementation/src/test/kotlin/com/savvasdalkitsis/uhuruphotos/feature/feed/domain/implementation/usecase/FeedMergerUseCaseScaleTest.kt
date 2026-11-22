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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedMergerUseCaseScaleTest {

    private val library = FeedLibraryFixture(
        days = 1000,
        remotePerDay = 50,
        syncedLocals = 15_000,
        localOnly = 5_000,
        queuedUploads = 2_000,
    )

    @Test
    fun `merges a large library without copying the upload queue for every local item`() {
        val (feed, bytes) = allocatedBytes { library.merger().mergeFeed() }

        assertEquals(55_000, feed.sumOf { it.mediaItems.size })
        assertTrue("allocated ${bytes / MB} MB", bytes < 64 * MB)
    }

    @Test
    fun `merges a large library without scanning the local media for every remote item`() {
        val (feed, nanos) = cpuNanos { library.merger().mergeFeed() }

        assertEquals(1_100, feed.size)
        assertTrue("took ${nanos / 1_000_000} ms", nanos < 3_000_000_000L)
    }

    private companion object {
        const val MB = 1024L * 1024L
    }
}
