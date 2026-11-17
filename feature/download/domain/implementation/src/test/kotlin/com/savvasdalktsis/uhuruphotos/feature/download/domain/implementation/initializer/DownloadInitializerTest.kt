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
package com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.initializer

import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import dagger.Lazy
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test

class DownloadInitializerTest {

    private val resolvedOn = CompletableDeferred<Thread>()
    private val downloadUseCase = mockk<DownloadUseCase>(relaxed = true) {
        every { observeDownloading() } returns flowOf(setOf("download"))
    }
    private val initializer = DownloadInitializer(Lazy {
        resolvedOn.complete(Thread.currentThread())
        downloadUseCase
    })

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `builds the download use case off the thread that creates the application`() = runBlocking {
        initializer.onAppCreated(mockk())

        assertNotSame(Thread.currentThread(), withTimeout(TIMEOUT) { resolvedOn.await() })
    }

    @Test
    fun `clears failed and stale downloads while media is downloading`() = runBlocking {
        initializer.onAppCreated(mockk())

        coVerify(timeout = TIMEOUT) { downloadUseCase.clearFailuresAndStale() }
    }

    private companion object {
        const val TIMEOUT = 5_000L
    }
}
