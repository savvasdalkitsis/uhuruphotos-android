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
package com.savvasdalkitsis.uhuruphotos.foundation.seam.api

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.HANDLED_ERROR_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import saschpe.log4k.Log
import saschpe.log4k.Logger
import java.util.concurrent.CopyOnWriteArrayList

class SeamTest {

    private val crashes = CopyOnWriteArrayList<Throwable>()
    private val reported = CopyOnWriteArrayList<Throwable>()
    private val reporter = object : Logger() {
        override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) {
            if (tag == HANDLED_ERROR_TAG && throwable != null) {
                reported += throwable
            }
        }
    }
    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val previousLoggers = Log.loggers.toList()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val underTest = Seam<Int, String>(
        actionHandler = { _, action -> handle(action) },
        initialState = 0,
        scope = scope,
    )

    @Before
    fun setUp() {
        Thread.setDefaultUncaughtExceptionHandler { _, e -> crashes += e }
        Log.loggers.clear()
        Log.loggers += reporter
    }

    @After
    fun tearDown() {
        scope.cancel()
        Log.loggers.clear()
        Log.loggers += previousLoggers
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
    }

    @Test
    fun `an action whose flow throws is reported instead of crashing and later actions still run`() = runBlocking {
        underTest.action("fail")
        awaitOutcome()
        underTest.action("increment")

        withTimeout(5000) { underTest.state.first { it == 1 } }
        assertEquals(emptyList<Throwable>(), crashes)
        assertEquals(listOf("action failed"), reported.map { it.message })
    }

    @Test
    fun `an action whose child coroutine fails is reported instead of crashing`() = runBlocking {
        underTest.action("fail in child")
        awaitOutcome()

        assertEquals(emptyList<Throwable>(), crashes)
        assertEquals(listOf("child failed"), reported.map { it.message })
    }

    @Test
    fun `an action failing with an error still crashes`() = runBlocking {
        underTest.action("error")
        awaitOutcome()

        assertEquals(listOf("fatal"), crashes.map { it.message })
        assertEquals(emptyList<Throwable>(), reported)
    }

    private suspend fun awaitOutcome() = withTimeout(5000) {
        while (crashes.isEmpty() && reported.isEmpty()) {
            delay(10)
        }
    }

    private fun handle(action: String): Flow<Mutation<Int>> = when (action) {
        "fail" -> flow { throw IllegalStateException("action failed") }
        "fail in child" -> flow {
            CoroutineScope(currentCoroutineContext()).launch { throw IllegalStateException("child failed") }
            awaitCancellation()
        }
        "error" -> flow { throw AssertionError("fatal") }
        else -> flowOf(Mutation { it + 1 })
    }
}
