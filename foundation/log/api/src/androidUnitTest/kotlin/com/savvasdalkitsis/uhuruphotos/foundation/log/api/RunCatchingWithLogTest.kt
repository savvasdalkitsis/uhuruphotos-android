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
package com.savvasdalkitsis.uhuruphotos.foundation.log.api

import com.github.michaelbull.result.getError
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import saschpe.log4k.Logger
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.cancellation.CancellationException
import saschpe.log4k.Log as Log4k

class RunCatchingWithLogTest {

    private val previousLoggers = Log4k.loggers.toList()
    private val reported = CopyOnWriteArrayList<Throwable>()

    @Before
    fun captureReports() {
        Log4k.loggers.clear()
        Log4k.loggers += object : Logger() {
            override fun print(level: Log4k.Level, tag: String, message: String?, throwable: Throwable?) {
                if (tag == HANDLED_ERROR_TAG && throwable != null) reported += throwable
            }
        }
    }

    @After
    fun restore() {
        Log4k.loggers.clear()
        Log4k.loggers += previousLoggers
    }

    @Test
    fun `returns a failure as an error result without reporting it`() {
        assertEquals("offline", runCatchingWithLog { throw IllegalStateException("offline") }.getError()?.message)
        assertEquals(emptyList<Throwable>(), reported.toList())
    }

    @Test
    fun `lets cancellation through`() {
        assertThrows(CancellationException::class.java) { runCatchingWithLog { throw CancellationException("left the screen") } }
        assertEquals(emptyList<Throwable>(), reported.toList())
    }

    @Test
    fun `returns a fatal error as an error result and reports it`() {
        val heap = OutOfMemoryError("heap")

        assertEquals(heap, runCatchingWithLog { throw heap }.getError())
        assertEquals(listOf<Throwable>(heap), reported.toList())
    }
}
