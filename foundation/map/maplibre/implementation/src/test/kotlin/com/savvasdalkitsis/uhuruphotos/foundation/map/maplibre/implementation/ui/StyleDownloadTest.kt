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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.concurrent.thread

class StyleDownloadTest {

    private val server = ServerSocket(0, 1, InetAddress.getLoopbackAddress())
    private val url = URL("http://127.0.0.1:${server.localPort}/maps/default.json")

    @After
    fun stopServer() {
        server.close()
    }

    @Test(timeout = 5_000)
    fun `gives up on a style server that never answers`() {
        assertThrows(SocketTimeoutException::class.java) {
            downloadStyleJson(url, timeoutMillis = 200)
        }
    }

    @Test(timeout = 5_000)
    fun `downloads the style json`() {
        thread {
            server.accept().use { socket ->
                socket.getInputStream().bufferedReader().let { request ->
                    while (request.readLine().orEmpty().isNotEmpty()) Unit
                }
                socket.getOutputStream().write(
                    "HTTP/1.1 200 OK\r\nContent-Length: 2\r\nConnection: close\r\n\r\n{}".toByteArray()
                )
            }
        }

        assertEquals("{}", downloadStyleJson(url, timeoutMillis = 2_000))
    }
}
