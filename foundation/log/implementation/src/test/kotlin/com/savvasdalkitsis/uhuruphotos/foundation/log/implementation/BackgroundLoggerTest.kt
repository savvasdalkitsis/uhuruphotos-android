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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import saschpe.log4k.FileLogger
import saschpe.log4k.FileLogger.Limit.Files
import saschpe.log4k.FileLogger.Rotate
import saschpe.log4k.Log
import java.io.File
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

@RunWith(RobolectricTestRunner::class)
class BackgroundLoggerTest {

    @get:Rule
    val temp = TemporaryFolder()

    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val uncaught = Collections.synchronizedList(mutableListOf<Throwable>())
    private val writer = LogWriter()

    @After
    fun restoreHandler() {
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
    }

    @Test
    fun `keeps writing after the logs folder was deleted`() {
        val logsFolder = File(temp.root, "logs")
        val underTest = BackgroundLogger(writer, logsFolder) {
            fileLogger(logsFolder).also { logsFolder.deleteRecursively() }
        }
        Thread.setDefaultUncaughtExceptionHandler { _, e -> uncaught += e }

        underTest.log(Log.Level.Info, "Test", "after clearing", null)
        awaitWrites()

        assertEquals(emptyList<Throwable>(), uncaught.toList())
        assertTrue(logged(logsFolder, "after clearing"))
    }

    @Test
    fun `reports a write it cannot recover to logcat only`() {
        val logsFolder = temp.newFile("logs")
        val underTest = BackgroundLogger(writer, logsFolder) { fileLogger(logsFolder) }
        Thread.setDefaultUncaughtExceptionHandler { _, e -> uncaught += e }

        underTest.log(Log.Level.Info, "Test", "unwritable", null)
        awaitWrites()

        assertEquals(emptyList<Throwable>(), uncaught.toList())
        assertEquals(1, ShadowLog.getLogsForTag(BackgroundLogger.TAG).size)
    }

    @Test
    fun `closes the log file after every line`() {
        val logsFolder = File(temp.root, "logs")
        val underTest = BackgroundLogger(writer, logsFolder) { fileLogger(logsFolder) }
        val openBefore = openFiles()

        repeat(LINES) { underTest.log(Log.Level.Info, "Test", "line $it", null) }
        awaitWrites()

        openBefore?.let { assertTrue(openFiles()!! - it < LINES) }
        assertTrue(logsFolder.deleteRecursively())
    }

    private fun openFiles() = File("/proc/self/fd").list()?.size

    private fun awaitWrites() {
        val written = CountDownLatch(1)
        writer.execute { written.countDown() }
        assertTrue(written.await(30, SECONDS))
    }

    private fun fileLogger(logsFolder: File) =
        FileLogger(rotate = Rotate.After(lines = 1000), limit = Files(1), logPath = logsFolder.absolutePath)

    private fun logged(logsFolder: File, line: String) = logsFolder.walk()
        .filter { it.isFile }
        .any { line in it.readText() }

    private companion object {
        const val LINES = 50
    }
}
