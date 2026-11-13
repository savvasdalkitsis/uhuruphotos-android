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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.could_not_find_app_to_open
import uhuruphotos_android.foundation.strings.api.generated.resources.general_error
import usecase.ToasterUseCase

class IntentLauncherTest {

    private val context = mockk<Context>(relaxed = true)
    private val toaster = mockk<ToasterUseCase>(relaxed = true)
    private val intent = mockk<Intent>(relaxed = true)
    private val fallbackIntent = mockk<Intent>(relaxed = true)
    private val underTest = IntentLauncher(context, toaster)

    @Test
    fun `starts the intent`() {
        underTest.launch(intent)

        verify { context.startActivity(intent) }
        verify(exactly = 0) { toaster.show(any()) }
    }

    @Test
    fun `tells the user when no app can open the intent`() {
        every { context.startActivity(intent) } throws ActivityNotFoundException()

        underTest.launch(intent)

        verify { toaster.show(string.could_not_find_app_to_open) }
    }

    @Test
    fun `shows an error instead of crashing when the uri grant is refused`() {
        every { context.startActivity(intent) } throws SecurityException("Permission Denial")

        underTest.launch(intent)

        verify { toaster.show(string.general_error) }
    }

    @Test
    fun `falls back when the intent is refused`() {
        every { context.startActivity(intent) } throws SecurityException("Permission Denial")

        underTest.launch(intent, fallbackIntent)

        verify { context.startActivity(fallbackIntent) }
    }

    @Test
    fun `falls back when no app can open the intent`() {
        every { context.startActivity(intent) } throws ActivityNotFoundException()

        underTest.launch(intent, fallbackIntent)

        verify { context.startActivity(fallbackIntent) }
    }
}
