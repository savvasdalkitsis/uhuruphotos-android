/*
Copyright 2023 Savvas Dalkitsis

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

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Looper
import androidx.tracing.Trace
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated

object TracingInitializer : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        val debuggable = (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val profileable = Build.VERSION.SDK_INT >= 29 && app.applicationInfo.isProfileableByShell

        if (Build.VERSION.SDK_INT != 28 && (debuggable || profileable)) {
            Looper.getMainLooper().setMessageLogging { log ->
                if (!Trace.isEnabled()) {
                    return@setMessageLogging
                }
                if (log.startsWith('>')) {
                    val label = buildSectionLabel(log)
                    Trace.beginSection(label.take(127))
                } else if (log.startsWith('<')) {
                    Trace.endSection()
                }
            }
        }
    }

    private fun buildSectionLabel(log: String): String {
        val logNoPrefix = log.removePrefix(">>>>> Dispatching to ")
        val indexOfWhat = logNoPrefix.lastIndexOf(": ")
        val indexOfCallback = logNoPrefix.indexOf("} ")

        val targetHandler = logNoPrefix.substring(0, indexOfCallback + 1)
        val callback = logNoPrefix.substring(indexOfCallback + 2, indexOfWhat)
            .removePrefix("DispatchedContinuation[Dispatchers.Main, Continuation at ")
            .removePrefix("DispatchedContinuation[Dispatchers.Main.immediate, Continuation at ")
        val what = logNoPrefix.substring(indexOfWhat + 2)

        return if (callback != "null") {
            "$callback $targetHandler $what"
        } else {
            "$targetHandler $what"
        }
    }
}