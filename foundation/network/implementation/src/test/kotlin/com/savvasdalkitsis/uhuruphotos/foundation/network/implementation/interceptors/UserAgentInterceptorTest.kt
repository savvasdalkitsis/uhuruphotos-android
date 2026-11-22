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
package com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.interceptors

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class UserAgentInterceptorTest {

    private val agent = System.getProperty("http.agent")
    private val sent = slot<Request>()
    private val chain = mockk<Interceptor.Chain> {
        every { request() } returns Request.Builder().url("https://server/api/albums/").build()
        every { proceed(capture(sent)) } returns mockk<Response>()
    }

    @After
    fun restoreAgent() {
        agent?.let { System.setProperty("http.agent", it) } ?: System.clearProperty("http.agent")
    }

    @Test
    fun `sends a user agent for a device model with characters headers cannot carry`() {
        System.setProperty("http.agent", "Dalvik/2.1.0 (Linux; U; Android 14; Ğalaxy Ö1 Build/UP1A)")

        UserAgentInterceptor(mockk { every { appVersion() } returns "0.0.275" }).intercept(chain)

        assertEquals(
            "UhuruPhotos/0.0.275 Dalvik/2.1.0 (Linux; U; Android 14; ?alaxy ?1 Build/UP1A)",
            sent.captured.header("User-agent"),
        )
    }
}
