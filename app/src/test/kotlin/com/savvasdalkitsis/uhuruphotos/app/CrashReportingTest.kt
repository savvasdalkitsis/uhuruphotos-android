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

import android.content.Context
import com.bugsnag.android.Configuration
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import saschpe.log4k.Logger

class CrashReportingTest {

    private val context = mockk<Context>()
    private val configuration = mockk<Configuration>(relaxed = true)
    private val loggers = mutableListOf<Logger>()
    private val started = mutableListOf<Pair<Context, Configuration>>()
    private val loggersWhenStarted = mutableListOf<Int>()

    @Test
    fun `starts bugsnag grouping crashes from instrumented processes`() {
        startBugsnag(context, configuration, ::start, loggers)

        verify { configuration.addOnSend(InstrumentedProcessOnSend) }
        assertEquals(listOf(context to configuration), started)
    }

    @Test
    fun `reports handled errors once bugsnag has started`() {
        startBugsnag(context, configuration, ::start, loggers)

        assertEquals(listOf(0), loggersWhenStarted)
        assertEquals(1, loggers.size)
        assertSame(BugsnagErrorLogger::class.java, loggers.single().javaClass)
    }

    private fun start(context: Context, configuration: Configuration) {
        started += context to configuration
        loggersWhenStarted += loggers.size
    }
}
