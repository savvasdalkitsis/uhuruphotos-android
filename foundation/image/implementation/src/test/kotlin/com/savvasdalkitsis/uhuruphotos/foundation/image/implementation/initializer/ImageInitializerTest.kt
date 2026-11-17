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
package com.savvasdalkitsis.uhuruphotos.foundation.image.implementation.initializer

import android.content.Context
import coil.Coil
import coil.ImageLoader
import dagger.Lazy
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class ImageInitializerTest {

    private val imageLoader = mockk<ImageLoader>()
    private var imageLoadersBuilt = 0
    private val okHttpBuiltOn = CompletableDeferred<Thread>()
    private val initializer = ImageInitializer(
        Lazy {
            imageLoadersBuilt++
            imageLoader
        },
        Lazy {
            okHttpBuiltOn.complete(Thread.currentThread())
            OkHttpClient.Builder()
        },
        Lazy { Interceptor { it.proceed(it.request()) } },
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Coil.reset()
        Dispatchers.resetMain()
    }

    @Test
    fun `builds the image loader only when coil first needs it`() {
        initializer.onAppCreated(mockk())

        assertEquals(0, imageLoadersBuilt)
        assertSame(imageLoader, Coil.imageLoader(mockk<Context>(relaxed = true)))
        assertEquals(1, imageLoadersBuilt)
    }

    @Test
    fun `builds the video http client off the thread that creates the application`() = runBlocking {
        initializer.onAppCreated(mockk())

        assertNotSame(Thread.currentThread(), withTimeout(5_000) { okHttpBuiltOn.await() })
    }
}
