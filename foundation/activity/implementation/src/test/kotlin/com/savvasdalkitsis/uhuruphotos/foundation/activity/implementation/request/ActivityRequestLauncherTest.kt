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
package com.savvasdalkitsis.uhuruphotos.foundation.activity.implementation.request

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.FragmentActivity
import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ActivityRequestLauncherTest {

    private val controller = Robolectric.buildActivity(FragmentActivity::class.java).setup()
    private var activity: FragmentActivity = controller.get()
    private val underTest = ActivityRequestLauncher(object : CurrentActivityHolder {
        override val currentActivity: FragmentActivity get() = activity
    })
    private val request = IntentSenderRequest.Builder(
        PendingIntent.getActivity(activity, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    ).build()

    @Test
    fun `a request made while another is in flight does not crash`() = runTest {
        val first = async { underTest.performRequest("first", request) }
        val second = async { underTest.performRequest("second", request) }
        runCurrent()

        respond("request_0", RESULT_OK)
        respond("request_1", RESULT_CANCELED)

        assertEquals(Ok(Unit), first.await())
        assertTrue(second.await().isErr)
        assertNull(savedStateProvider())
    }

    @Test
    fun `a request made after a cancelled one does not crash`() = runTest {
        val cancelled = async { underTest.performRequest("cancelled", request) }
        runCurrent()
        cancelled.cancel()
        runCurrent()

        val next = async { underTest.performRequest("next", request) }
        runCurrent()
        respond("request_1", RESULT_OK)

        assertEquals(Ok(Unit), next.await())
        assertNull(savedStateProvider())
    }

    @Test
    fun `a cancelled request leaves nothing registered`() = runTest {
        val cancelled = async { underTest.performRequest("cancelled", request) }
        runCurrent()
        cancelled.cancel()
        runCurrent()

        assertNull(savedStateProvider())
    }

    @Test
    fun `a request restored after the activity is recreated waits for its result without relaunching`() = runTest {
        val beforeRecreation = async { underTest.performRequest("restored", request) }
        runCurrent()
        activity = controller.recreate().get()

        val restored = async { underTest.performRequest("restored", request) }
        runCurrent()
        respond("request_0", RESULT_OK)

        assertEquals(Ok(Unit), restored.await())
        beforeRecreation.cancel()
    }

    @Test
    fun `fails without an activity`() = runTest {
        val launcher = ActivityRequestLauncher(object : CurrentActivityHolder {
            override val currentActivity: FragmentActivity? = null
        })

        assertTrue(launcher.performRequest("orphan", request).isErr)
    }

    private fun savedStateProvider() =
        activity.savedStateRegistry.getSavedStateProvider("SAVED_STATE_REGISTRY_KEY")

    private fun respond(key: String, resultCode: Int) {
        val state = Bundle().also { activity.activityResultRegistry.onSaveInstanceState(it) }
        val keys = state.getStringArrayList("KEY_COMPONENT_ACTIVITY_REGISTERED_KEYS")!!
        val codes = state.getIntegerArrayList("KEY_COMPONENT_ACTIVITY_REGISTERED_RCS")!!
        activity.activityResultRegistry.dispatchResult(codes[keys.indexOf(key)], resultCode, null)
    }
}
