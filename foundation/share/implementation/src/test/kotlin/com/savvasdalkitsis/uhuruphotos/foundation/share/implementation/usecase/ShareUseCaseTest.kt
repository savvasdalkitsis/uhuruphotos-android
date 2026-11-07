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
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class ShareUseCaseTest {

    private val contentUri = "content://media/external/video/media/1"
    private val uri: Uri = mockk()
    private val context: Context = mockk()
    private val underTest = ShareUseCase(mockk(), mockk(relaxed = true), mockk(), mockk(), context)

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
    fun `shares content of unknown type as a photo`() = runTest {
        contentOfType(null)

        underTest.share(contentUri)

        verify { anyConstructed<Intent>().setDataAndType(uri, "image/jpeg") }
    }
}
