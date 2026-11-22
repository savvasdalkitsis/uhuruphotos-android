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
package com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class FlowThrottlingTest {

    private val rapidUpdates = flow {
        repeat(50) {
            emit(it)
            delay(100)
        }
    }

    @Test
    fun `passes the first update through straight away`() = runTest {
        var firstAt = -1L

        flow { emit(1) }.atMostOncePer(1.seconds).collect { if (firstAt < 0) firstAt = currentTime }

        assertEquals(0L, firstAt)
    }

    @Test
    fun `keeps only the latest of rapid updates once per period`() = runTest {
        val updates = rapidUpdates.atMostOncePer(1.seconds).toList()

        assertEquals(listOf(0, 9, 19, 29, 39, 49), updates)
    }
}
