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
package com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.interceptors.UserAgentInterceptor
import io.mockk.every
import io.mockk.mockk
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit.SECONDS

class NetworkModuleTest {

    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val crashes = CopyOnWriteArrayList<Throwable>()
    private val failure = CompletableFuture<IOException>()

    @After
    fun restoreHandler() {
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
    }

    @Test
    fun `fails a call instead of crashing when an interceptor throws`() {
        Thread.setDefaultUncaughtExceptionHandler { _, e -> crashes += e }
        val client = NetworkModule()
            .basicOkHttpBuilder(UserAgentInterceptor(mockk { every { appVersion() } returns "1" }))
            .addInterceptor { throw IllegalArgumentException("Unexpected char 0xfc in Cookie value") }
            .build()

        client.newCall(Request.Builder().url("https://localhost/api/albums/").build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { failure.complete(e) }
            override fun onResponse(call: Call, response: Response) { response.close() }
        })
        failure.get(5, SECONDS)
        client.dispatcher.executorService.shutdown()
        assertTrue(client.dispatcher.executorService.awaitTermination(5, SECONDS))

        assertEquals(emptyList<Throwable>(), crashes.toList())
    }
}
