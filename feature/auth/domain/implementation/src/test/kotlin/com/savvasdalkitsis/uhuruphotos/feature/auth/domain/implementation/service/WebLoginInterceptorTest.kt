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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies.WebCookies
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.api.navigation.WebLoginNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WebLoginInterceptorTest {

    private val request = Request.Builder().url("https://server/api/albums/").build()
    private val navigator = mockk<Navigator>(relaxed = true)
    private val serverUseCase = mockk<ServerUseCase> {
        every { getServerUrl() } returns "https://server"
    }
    private val cookies = mockk<WebCookies>(relaxed = true)
    private val underTest = WebLoginInterceptor(navigator, serverUseCase, cookies)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun respond(code: Int, location: String? = null) = mockk<Interceptor.Chain> {
        every { request() } returns request
        every { proceed(request) } returns Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("")
            .apply { location?.let { header("Location", it) } }
            .build()
    }

    @Test
    fun `a redirect without a location does not crash`() = runTest {
        underTest.intercept(respond(302))

        verify(exactly = 0) { navigator.navigateTo(any<WebLoginNavigationRoute>()) }
    }

    @Test
    fun `a redirect opens the web login at its location`() = runTest {
        underTest.intercept(respond(302, "https://auth/login"))

        verify { cookies.setCookie("https://server", "") }
        verify { navigator.navigateTo(WebLoginNavigationRoute("https://auth/login")) }
    }

    @Test
    fun `other responses pass through`() = runTest {
        underTest.intercept(respond(200))

        verify(exactly = 0) { navigator.navigateTo(any<WebLoginNavigationRoute>()) }
    }
}
