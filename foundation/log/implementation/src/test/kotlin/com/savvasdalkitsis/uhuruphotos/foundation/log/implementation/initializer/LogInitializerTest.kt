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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.initializer

import android.app.Application
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.BackgroundLogger
import com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.LogWriter
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import saschpe.log4k.Log
import java.io.File
import java.util.Collections
import java.util.concurrent.CountDownLatch
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log as LogSwitch

@RunWith(RobolectricTestRunner::class)
class LogInitializerTest {

    private val app: Application = RuntimeEnvironment.getApplication()
    private val settings = mockk<SettingsUseCase> { every { getLoggingEnabled() } returns true }
    private val previousLoggers = Log.loggers.toList()
    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val wasEnabled = LogSwitch.enabled
    private val writer = LogWriter()
    private val underTest = LogInitializer(app, settings, writer)

    @After
    fun tearDown() {
        Log.loggers.clear()
        Log.loggers += previousLoggers
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
        LogSwitch.enabled = wasEnabled
    }

    @Test
    fun `logging leaves the log file write to a background thread`() {
        val writing = CountDownLatch(1)
        underTest.onAppCreated(app)
        writer.execute { writing.await() }

        Log.verbose(tag = "Test", message = "a line")

        assertFalse(logged("a line"))
        writing.countDown()
        writer.drain()
        assertTrue(logged("a line"))
    }

    @Test
    fun `logs a crash to the file before handing it on`() {
        val loggedWhenHandedOn = mutableListOf<Boolean>()
        Thread.setDefaultUncaughtExceptionHandler { _, _ -> loggedWhenHandedOn += logged("boom") }
        underTest.onAppCreated(app)

        Thread.getDefaultUncaughtExceptionHandler()!!.uncaughtException(Thread.currentThread(), IllegalStateException("boom"))

        assertEquals(listOf(true), loggedWhenHandedOn)
    }

    @Test
    fun `hands a crash on once when the log file cannot be written`() {
        val handedOn = Collections.synchronizedList(mutableListOf<Throwable>())
        File(app.filesDir, "logs").apply { deleteRecursively() }.writeText("")
        Thread.setDefaultUncaughtExceptionHandler { _, e -> handedOn += e }
        underTest.onAppCreated(app)

        Thread.getDefaultUncaughtExceptionHandler()!!.uncaughtException(Thread.currentThread(), IllegalStateException("boom"))
        writer.drain()

        assertEquals(listOf("boom"), handedOn.map { it.message })
        File(app.filesDir, "logs").delete()
    }

    @Test
    fun `adds a single logger without file access on the calling thread`() {
        underTest.onAppCreated(app)

        assertEquals(previousLoggers.size + 1, Log.loggers.size)
        assertTrue(Log.loggers.last() is BackgroundLogger)
    }

    private fun logged(line: String) = File(app.filesDir, "logs").walk()
        .filter { it.isFile }
        .any { line in it.readText() }
}
