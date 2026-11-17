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
import com.bugsnag.android.OnSendCallback

internal const val INSTRUMENTED_PROCESS_GROUPING_HASH = "instrumented-process"

private const val FRIDA_THREAD_PREFIX = "frida-"
private const val FRIDA_JAVASCRIPT_LOOP = "gum-js-loop"
private const val GLIB_MAIN_LOOP = "gmain"

internal fun instrumentationThreads(threadNames: List<String>): List<String> {
    val fridaThreads = threadNames.filter { it.startsWith(FRIDA_THREAD_PREFIX) || it == FRIDA_JAVASCRIPT_LOOP }
    return when {
        fridaThreads.isEmpty() -> emptyList()
        else -> threadNames.filter { it in fridaThreads || it == GLIB_MAIN_LOOP }.distinct()
    }
}

internal object InstrumentedProcessOnSend : OnSendCallback {

    override fun onSend(event: Event): Boolean {
        val matchedThreads = instrumentationThreads(event.threads.map { it.name })
        if (matchedThreads.isNotEmpty()) {
            event.addMetadata("app", "instrumented", true)
            event.addMetadata("app", "instrumentationThreads", matchedThreads)
            event.groupingHash = INSTRUMENTED_PROCESS_GROUPING_HASH
        }
        return true
    }
}

internal fun Configuration.groupInstrumentedProcessCrashes() {
    addOnSend(InstrumentedProcessOnSend)
}
