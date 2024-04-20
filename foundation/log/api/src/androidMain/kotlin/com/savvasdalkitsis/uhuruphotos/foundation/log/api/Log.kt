@file:JvmName("LogAndroid")
/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.log.api

import com.michaelflisar.lumberjack.core.L

actual fun log(tag: String, msg: () -> String) {
    tryIgnore {
        if (tag.isNotEmpty()) {
            L.tag(tag).v(msg)
        } else {
            L.v(msg)
        }
    }
}

actual fun log(t: Throwable) {
    try {
        tempEnable {
            L.w(t)
        }
    } catch (_: Exception) {
        t.printStackTrace()
    }
}

actual fun log(t: Throwable, msg: () -> String) {
    try {
        tempEnable {
            L.w(t, msg)
        }
    } catch (_: Exception) {
        println("$msg")
        t.printStackTrace()
    }
}

actual fun logError(t: Throwable) {
    try {
        tempEnable {
            L.e(t)
        }
    }  catch (_: Exception) {
        t.printStackTrace()
    }
}

actual object Log {
    actual var enabled: Boolean
        get() = L.isEnabled()
        set(value) {
            L.enable(value)
        }
}

private inline fun tryIgnore(block: () -> Unit) {
    try {
        block()
    } catch (_: Exception) {
    }
}

private inline fun tempEnable(log: () -> Unit) {
    val old = Log.enabled
    Log.enabled = true
    log()
    Log.enabled = old
}

