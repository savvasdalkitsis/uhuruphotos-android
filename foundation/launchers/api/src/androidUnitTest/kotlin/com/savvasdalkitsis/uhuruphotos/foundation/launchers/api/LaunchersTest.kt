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
package com.savvasdalkitsis.uhuruphotos.foundation.launchers.api

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.HANDLED_ERROR_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import saschpe.log4k.Log
import saschpe.log4k.Logger
import java.util.concurrent.CopyOnWriteArrayList

@OptIn(ExperimentalCoroutinesApi::class)
class LaunchersTest {

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

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Thread.setDefaultUncaughtExceptionHandler { _, e -> crashes += e }
        Log.loggers.clear()
        Log.loggers += reporter
    }

    @After
    fun tearDown() {
        Log.loggers.clear()
        Log.loggers += previousLoggers
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
        Dispatchers.resetMain()
    }

    @Test
    fun `failures on the main thread are reported instead of crashing`() {
        onMain { throw IllegalStateException("main failed") }

        assertEquals(emptyList<Throwable>(), crashes)
        assertEquals(listOf("main failed"), reported.map { it.message })
    }

    @Test
    fun `failures on the io thread are reported instead of crashing`() = runBlocking {
        onIO { throw IllegalStateException("io failed") }.join()

        assertEquals(emptyList<Throwable>(), crashes)
        assertEquals(listOf("io failed"), reported.map { it.message })
    }

    @Test
    fun `errors still crash`() = runBlocking {
        onIO { throw AssertionError("fatal") }.join()

        assertEquals(listOf("fatal"), crashes.map { it.message })
        assertEquals(emptyList<Throwable>(), reported)
    }
}
