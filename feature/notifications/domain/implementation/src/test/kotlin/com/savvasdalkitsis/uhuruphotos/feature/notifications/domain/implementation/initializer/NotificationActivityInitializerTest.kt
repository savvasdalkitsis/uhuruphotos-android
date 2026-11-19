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
package com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.initializer

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Looper
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import com.savvasdalkitsis.uhuruphotos.feature.notifications.domain.implementation.model.NotificationRequest.ACCEPTED
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class NotificationActivityInitializerTest {

    private val launchedOn = mutableListOf<Thread>()
    private lateinit var answer: ActivityResultCallback<Boolean>
    private val launcher = object : ActivityResultLauncher<String>() {
        override val contract: ActivityResultContract<String, *> = RequestPermission()
        override fun launch(input: String, options: ActivityOptionsCompat?) {
            launchedOn += Thread.currentThread()
            answer.onActivityResult(true)
        }
        override fun unregister() = Unit
    }
    private val activity = mockk<FragmentActivity> {
        every { registerForActivityResult(any<RequestPermission>(), any<ActivityResultCallback<Boolean>>()) } answers {
            answer = secondArg()
            launcher
        }
    }
    private val underTest = NotificationActivityInitializer()

    @Test
    fun `asks for a permission requested off the main thread on the main thread`() {
        underTest.onActivityCreated(activity)

        val result = CoroutineScope(Dispatchers.Default).async { underTest.askForPermission(POST_NOTIFICATIONS) }
        val deadline = System.currentTimeMillis() + 5000
        while (!result.isCompleted && System.currentTimeMillis() < deadline) {
            shadowOf(Looper.getMainLooper()).idle()
        }

        assertEquals(listOf(Looper.getMainLooper().thread), launchedOn)
        assertEquals(ACCEPTED, result.getCompleted())
    }
}
