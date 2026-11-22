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

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.cookies.WebCookies
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.AuthenticationHeadersUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthenticationHeaderInjectorTest {

    private val repository = mockk<AuthenticationRepository> { coEvery { getAccessToken() } returns "token" }
    private val cookies = mockk<WebCookies>()
    private val chain = mockk<Interceptor.Chain> {
        every { request() } returns Request.Builder().url("https://server/api/albums/").build()
    }
    private val underTest = AuthenticationHeaderInjector(AuthenticationHeadersUseCase(repository, cookies))

    @Test
    fun `leaves out cookies that headers cannot carry`() {
        every { cookies.getCookie(any()) } returns "csrftoken=abc; name=Jürgen; sessionid=xyz;"

        val request = underTest.inject(chain)

        assertEquals("csrftoken=abc; sessionid=xyz", request.header("Cookie"))
        assertEquals("Bearer token", request.header("Authorization"))
    }

    @Test
    fun `leaves out the cookie header when no cookie can be carried`() {
        every { cookies.getCookie(any()) } returns "name=Jürgen"

        assertEquals(null, underTest.inject(chain).header("Cookie"))
    }
}
