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
package com.savvasdalkitsis.uhuruphotos.app

import com.bugsnag.android.Configuration
import com.bugsnag.android.Event
import com.bugsnag.android.Thread
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InstrumentedProcessTest {

    private val normalProcessThreads = listOf(
        "sis.uhuruphotos",
        "Signal Catcher",
        "perfetto_hprof_",
        "ADB-JDWP Connec",
        "Jit thread pool",
        "HeapTaskDaemon",
        "Binder:10032_1",
        "Profile Saver",
        "Bugsnag Span Ta",
        "Thread-61",
        "WM.task-1",
        "DefaultDispatch",
        "RenderThread",
        "Bugsnag Error t",
        "OkHttp Connecti",
        "CronetInit",
    )
    private val fridaGadgetThreads = normalProcessThreads.take(9) +
        listOf("frida-gadget", "gmain") +
        normalProcessThreads.drop(9)

    @Test
    fun `matches the frida gadget and its glib loop from the reported crashes`() {
        assertEquals(listOf("frida-gadget", "gmain"), instrumentationThreads(fridaGadgetThreads))
    }

    @Test
    fun `matches a frida javascript loop`() {
        assertEquals(listOf("gum-js-loop"), instrumentationThreads(normalProcessThreads + "gum-js-loop"))
    }

    @Test
    fun `matches any frida prefixed thread`() {
        assertEquals(listOf("frida-agent-mai"), instrumentationThreads(normalProcessThreads + "frida-agent-mai"))
    }

    @Test
    fun `does not match a normal process`() {
        assertEquals(emptyList<String>(), instrumentationThreads(normalProcessThreads))
    }

    @Test
    fun `does not match a glib loop without frida`() {
        assertEquals(emptyList<String>(), instrumentationThreads(normalProcessThreads + "gmain"))
    }

    @Test
    fun `does not match threads that only resemble frida`() {
        assertEquals(emptyList<String>(), instrumentationThreads(normalProcessThreads + listOf("friday-sync", "Frida", "gum")))
    }

    @Test
    fun `tags and groups events from an instrumented process`() {
        val event = eventWithThreads(fridaGadgetThreads)
        every { event.addMetadata(any(), any(), any()) } returns Unit
        every { event.groupingHash = any() } returns Unit

        assertTrue(InstrumentedProcessOnSend.onSend(event))

        verify { event.addMetadata("app", "instrumented", true) }
        verify { event.addMetadata("app", "instrumentationThreads", listOf("frida-gadget", "gmain")) }
        verify { event.groupingHash = INSTRUMENTED_PROCESS_GROUPING_HASH }
    }

    @Test
    fun `leaves events from a normal process untouched`() {
        val event = eventWithThreads(normalProcessThreads + "gmain")

        assertTrue(InstrumentedProcessOnSend.onSend(event))
    }

    @Test
    fun `registers the instrumented process callback`() {
        val configuration = mockk<Configuration>(relaxed = true)

        configuration.groupInstrumentedProcessCrashes()

        verify { configuration.addOnSend(InstrumentedProcessOnSend) }
    }

    private fun eventWithThreads(names: List<String>): Event = mockk {
        every { threads } returns names.map { threadName -> mockk<Thread> { every { name } returns threadName } }
    }
}
