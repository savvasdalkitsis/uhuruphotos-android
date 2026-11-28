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
package com.savvasdalkitsis.uhuruphotos.foundation.tracing.implementation.initializer

import android.content.pm.ApplicationInfo
import android.os.Looper
import android.util.Printer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowTrace
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TracingInitializerTest {

    private val app = RuntimeEnvironment.getApplication()

    @After
    fun tearDown() {
        Looper.getMainLooper().setMessageLogging(null)
        ShadowTrace.reset()
    }

    private fun mainLooperLogging(): Printer? = ReflectionHelpers.getField(Looper.getMainLooper(), "mLogging")

    private fun build(debuggable: Boolean, tracing: Boolean) {
        val info = app.applicationInfo
        info.flags = if (debuggable) {
            info.flags or ApplicationInfo.FLAG_DEBUGGABLE
        } else {
            info.flags and ApplicationInfo.FLAG_DEBUGGABLE.inv()
        }
        val profileableByShell = ReflectionHelpers.getStaticField<Int>(
            ApplicationInfo::class.java, "PRIVATE_FLAG_PROFILEABLE_BY_SHELL"
        )
        val privateFlags = ReflectionHelpers.getField<Int>(info, "privateFlags")
        ReflectionHelpers.setField(info, "privateFlags", privateFlags or profileableByShell)
        assertTrue(info.isProfileableByShell)
        ShadowTrace.setEnabled(tracing)
        TracingInitializer().onAppCreated(app)
    }

    @Test
    fun `leaves main thread messages alone in a release build nobody is tracing`() {
        build(debuggable = false, tracing = false)

        assertNull(mainLooperLogging())
    }

    @Test
    fun `traces main thread messages in a debug build`() {
        build(debuggable = true, tracing = false)

        assertNotNull(mainLooperLogging())
    }

    @Test
    fun `traces main thread messages of a release build a profiler traces from the start`() {
        build(debuggable = false, tracing = true)

        mainLooperLogging()!!.run {
            println(">>>>> Dispatching to Handler (android.os.Handler) {5d1f} kotlinx.Task@1: 0")
            println("<<<<< Finished to Handler (android.os.Handler) {5d1f} kotlinx.Task@1")
            println(">>>>> Dispatching to Handler (android.view.Choreographer) {9a2c} null: 1")
            println("<<<<< Finished to Handler (android.view.Choreographer) {9a2c} null")
        }

        assertEquals(
            listOf(
                "kotlinx.Task@1 Handler (android.os.Handler) {5d1f} 0",
                "Handler (android.view.Choreographer) {9a2c} 1",
            ),
            ShadowTrace.getPreviousSections().toList(),
        )
    }

    @Test
    fun `ignores main thread messages while tracing stops`() {
        build(debuggable = true, tracing = true)
        ShadowTrace.setEnabled(false)

        mainLooperLogging()!!.println(">>>>> Dispatching to Handler (android.os.Handler) {5d1f} null: 0")

        assertEquals(emptyList<String>(), ShadowTrace.getCurrentSections().toList())
    }
}
