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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import saschpe.log4k.Log
import saschpe.log4k.Logger
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.EmptyCoroutineContext
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log as LogSwitch

class CookieMonitorTest {

    private val logged = CopyOnWriteArrayList<String>()
    private val previousLoggers = Log.loggers.toList()
    private val wasEnabled = LogSwitch.enabled
    private val serverUseCase = mockk<ServerUseCase> { every { getServerUrl() } returns "https://server" }
    private val cookies = mockk<WebCookies> {
        every { getCookie("https://server") } returnsMany listOf(
            "csrftoken=csrf-secret; sessionid=session-secret",
            "sessionid=session-secret; authelia_session=auth-secret",
        )
    }

    @Before
    fun captureLogs() {
        LogSwitch.enabled = true
        Log.loggers.clear()
        Log.loggers += object : Logger() {
            override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) {
                logged += message.orEmpty()
            }
        }
    }

    @After
    fun restoreLogs() {
        Log.loggers.clear()
        Log.loggers += previousLoggers
        LogSwitch.enabled = wasEnabled
    }

    @Test
    fun `logs the names of the cookies it waits on but never their values`() = runBlocking {
        CookieMonitor(serverUseCase, cookies).monitor(EmptyCoroutineContext).join()

        assertEquals(2, logged.size)
        assertTrue(logged.toString(), logged.none { "secret" in it })
        assertTrue(logged.toString(), "authelia_session" in logged.last())
    }
}
