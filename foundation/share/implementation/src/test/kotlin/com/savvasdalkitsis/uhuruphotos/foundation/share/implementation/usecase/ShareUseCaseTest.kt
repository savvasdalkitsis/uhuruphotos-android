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
package com.savvasdalkitsis.uhuruphotos.foundation.share.implementation.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import io.mockk.every
import io.mockk.slot
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ShareUseCaseTest {

    private val contentUri = "content://media/external/video/media/1"
    private val uri: Uri = mockk()
    private val context: Context = mockk()
    private val settings = mockk<SettingsUseCase> {
        every { getShareRemoveGpsData() } returns false
    }
    private val request = slot<ImageRequest>()
    private val result: ImageResult = mockk()
    private val downloaded: Disposable = mockk { every { job } returns CompletableDeferred(result) }
    private val imageLoader: ImageLoader = mockk { every { enqueue(capture(request)) } returns downloaded }
    private val underTest = ShareUseCase(
        mockk { every { openSnapshot(any()) } returns null },
        mockk(relaxed = true),
        imageLoader,
        settings,
        context,
        mockk(),
    )

    @Before
    fun setUp() {
        mockkStatic(Uri::class, Intent::class)
        every { Uri.parse(contentUri) } returns uri
        every { Intent.createChooser(any(), any()) } returns mockk(relaxed = true)
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().setDataAndType(any(), any()) } answers { self as Intent }
        every { anyConstructed<Intent>().putExtra(any<String>(), any<Parcelable>()) } answers { self as Intent }
        every { anyConstructed<Intent>().addFlags(any()) } answers { self as Intent }
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    private fun contentOfType(type: String?) {
        every { context.contentResolver.getType(uri) } returns type
    }

    @Test
    fun `shares local videos as videos`() = runTest {
        contentOfType("video/mp4")

        underTest.share(contentUri)

        verify { anyConstructed<Intent>().setDataAndType(uri, "video/mp4") }
    }

    @Test
    fun `downloads server photos without keeping them decoded in memory`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))

        underTest.share("https://server/media/photos/1")

        assertEquals(CachePolicy.DISABLED, request.captured.memoryCachePolicy)
    }

    @Test
    fun `shares content of unknown type as a photo`() = runTest {
        contentOfType(null)

        underTest.share(contentUri)

        verify { anyConstructed<Intent>().setDataAndType(uri, "image/jpeg") }
    }
}
