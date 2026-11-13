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
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.disk.DiskCache
import coil.request.ImageResult
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toOkioPath
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.could_not_remove_gps_data
import usecase.ToasterUseCase
import java.io.File

@RunWith(RobolectricTestRunner::class)
class ShareUseCaseGpsTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val url = "https://server/media/photos/1"
    private val cached = File(context.cacheDir, "cached-photo")
    private val shared = File(context.cacheDir, "share_cache/Photo-0.jpg")
    private val diskCache = mockk<DiskCache> {
        every { openSnapshot(url) } answers {
            mockk(relaxed = true) { every { data } returns cached.toOkioPath() }
        }
    }
    private val imageLoader = mockk<ImageLoader> {
        every { enqueue(any()) } returns mockk { every { job } returns CompletableDeferred(mockk<ImageResult>()) }
    }
    private val navigator = mockk<Navigator>(relaxed = true)
    private val toaster = mockk<ToasterUseCase>(relaxed = true)
    private val settings = mockk<SettingsUseCase> {
        every { getShareRemoveGpsData() } returns true
    }
    private val underTest = ShareUseCase(diskCache, navigator, imageLoader, settings, context, toaster)

    @Before
    fun setUp() {
        mockkStatic(FileProvider::class)
        every { FileProvider.getUriForFile(any(), any(), any()) } answers { Uri.fromFile(thirdArg()) }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun givenCached(resource: String) {
        javaClass.classLoader!!.getResourceAsStream(resource).use { input ->
            cached.outputStream().use { input.copyTo(it) }
        }
    }

    @Test
    fun `refuses to share a photo whose gps data cannot be removed`() = runTest {
        givenCached("photo.gif")

        underTest.share(url)

        verify(exactly = 0) { navigator.navigateTo(any<Intent>()) }
        verify { toaster.show(string.could_not_remove_gps_data) }
        assertFalse(shared.exists())
    }

    @Test
    fun `shares a photo without its gps data`() = runTest {
        givenCached("photo.jpg")
        ExifInterface(cached).apply {
            setLatLong(51.5, -0.12)
            saveAttributes()
        }

        underTest.share(url)

        verify { navigator.navigateTo(any<Intent>()) }
        assertNull(ExifInterface(shared).latLong)
    }
}
