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

import android.util.AndroidRuntimeException
import android.webkit.CookieManager
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.AuthenticationHeadersUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext

class WebCookiesTest {

    private val url = "https://server/"
    private val missingWebView = WebCookies {
        throw AndroidRuntimeException("MissingWebViewPackageException: Failed to load WebView provider: No WebView installed")
    }
    private val cookieManager = mockk<CookieManager>(relaxed = true)
    private val webCookies = WebCookies { cookieManager }

    @Before
    fun setUp() {
        mockkStatic(CookieManager::class)
        every { CookieManager.getInstance() } answers { throw AndroidRuntimeException("MissingWebViewPackageException") }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `the authenticated cookie jar can be created while WebView is missing`() {
        val jar = WebkitCookieManager(WebCookies())

        assertEquals(emptyList<Cookie>(), jar.loadForRequest(url.toHttpUrl()))
    }

    @Test
    fun `saving cookies while WebView is missing is skipped`() {
        WebkitCookieManager(missingWebView).saveFromResponse(url.toHttpUrl(), listOf(Cookie.parse(url.toHttpUrl(), "a=b")!!))
    }

    @Test
    fun `reads and writes cookies through WebView`() {
        every { cookieManager.getCookie(url) } returns "a=b"

        webCookies.setCookie(url, "c=d")

        assertEquals("a=b", webCookies.getCookie(url))
        verify { cookieManager.setCookie(url, "c=d") }
    }

    @Test
    fun `ignores cookies without a server`() {
        webCookies.setCookie(null, "")

        assertEquals(null, webCookies.getCookie(null))
        verify(exactly = 0) { cookieManager.setCookie(any(), any()) }
    }

    @Test
    fun `authentication headers skip cookies while WebView is missing`() {
        val repository = mockk<AuthenticationRepository> {
            coEvery { getAccessToken() } returns "token"
        }

        val headers = AuthenticationHeadersUseCase(repository, missingWebView).headers(url)

        assertEquals(setOf("Authorization" to "Bearer token"), headers)
    }

    @Test
    fun `cookie monitor stops instead of crashing while WebView is missing`() = runBlocking {
        val serverUseCase = mockk<ServerUseCase> {
            every { getServerUrl() } returns url
        }

        CookieMonitor(serverUseCase, missingWebView).monitor(EmptyCoroutineContext).join()
    }
}
