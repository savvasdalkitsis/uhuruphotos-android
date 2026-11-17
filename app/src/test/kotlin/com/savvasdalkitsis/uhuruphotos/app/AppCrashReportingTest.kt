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

import android.app.Application
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AppCrashReportingTest {

    private val app = App()
    private val injectedWhenCrashReportingStarted = mutableListOf<Boolean>()

    @Before
    fun interceptCrashReporting() {
        mockkStatic(CRASH_REPORTING)
        every { any<Application>().startCrashReporting() } answers {
            injectedWhenCrashReportingStarted += runCatching { app.applicationInitializer }.isSuccess
        }
    }

    @After
    fun restoreCrashReporting() {
        unmockkStatic(CRASH_REPORTING)
    }

    @Test
    fun `starts crash reporting while the application is created before its dependency graph`() {
        runCatching { app.onCreate() }

        assertEquals(listOf(false), injectedWhenCrashReportingStarted)
    }

    private companion object {
        const val CRASH_REPORTING = "com.savvasdalkitsis.uhuruphotos.app.CrashReportingKt"
    }
}
