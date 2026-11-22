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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.network

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class DynamicDomainInterceptorTest {

    private val serverUseCase = mockk<ServerUseCase>()
    private val chain = mockk<Interceptor.Chain> {
        every { request() } returns Request.Builder().url("https://localhost/api/albums/").build()
    }

    @Test
    fun `fails the request instead of crashing when the server url cannot be parsed`() {
        every { serverUseCase.getServerUrl() } returns "https://photos.example.com:99999"

        assertThrows(IOException::class.java) { DynamicDomainInterceptor(serverUseCase).intercept(chain) }
    }
}
