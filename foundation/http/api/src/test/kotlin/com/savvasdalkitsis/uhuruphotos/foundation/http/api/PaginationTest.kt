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
package com.savvasdalkitsis.uhuruphotos.foundation.http.api

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PaginationTest {

    @Test
    fun `follows next until the last page`() = runTest {
        val pages = mapOf(1 to (listOf("a", "b") to "?page=2"), 2 to (listOf("c") to null))
        val requested = mutableListOf<Int>()

        val items = allPages { page -> requested += page; pages.getValue(page) }

        assertEquals(listOf("a", "b", "c"), items)
        assertEquals(listOf(1, 2), requested)
    }

    @Test
    fun `stops at an empty page even when next is set`() = runTest {
        val items = allPages<String> { emptyList<String>() to "?page=2" }

        assertEquals(emptyList<String>(), items)
    }
}
