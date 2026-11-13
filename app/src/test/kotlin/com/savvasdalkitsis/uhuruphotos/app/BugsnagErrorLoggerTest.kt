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
package com.savvasdalkitsis.uhuruphotos.app

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.HANDLED_ERROR_TAG
import org.junit.Assert.assertEquals
import org.junit.Test
import saschpe.log4k.Log
import saschpe.log4k.Logger

class BugsnagErrorLoggerTest {

    private val notified = mutableListOf<Throwable>()
    private val underTest = BugsnagErrorLogger(notify = { notified += it })
    private val error = IllegalStateException("boom")

    @Test
    fun `notifies handled errors`() {
        underTest.log(Log.Level.Error, HANDLED_ERROR_TAG, "Handled error", error)

        assertEquals(listOf(error), notified)
    }

    @Test
    fun `ignores errors logged under other tags`() {
        underTest.log(Log.Level.Error, "Other", "Crash", error)

        assertEquals(emptyList<Throwable>(), notified)
    }

    @Test
    fun `ignores handled error messages without a throwable`() {
        underTest.log(Log.Level.Error, HANDLED_ERROR_TAG, "Handled error", null)

        assertEquals(emptyList<Throwable>(), notified)
    }

    @Test
    fun `registers once ahead of other loggers`() {
        val other = object : Logger() {
            override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) = Unit
        }
        val loggers = mutableListOf<Logger>(other)

        reportHandledErrorsToBugsnag(loggers)
        reportHandledErrorsToBugsnag(loggers)

        assertEquals(2, loggers.size)
        assertEquals(true, loggers.first() is BugsnagErrorLogger)
    }
}
