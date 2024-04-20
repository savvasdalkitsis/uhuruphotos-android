@file:JvmName("LogJvm")
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

import java.util.logging.Level
import java.util.logging.Logger

actual fun log(tag: String, msg: () -> String) {
    maybe {
        log(Level.INFO, msg())
    }
}

actual fun log(t: Throwable) {
    log(t) { t.message.orEmpty() }
}

actual fun log(t: Throwable, msg: () -> String) {
    maybe {
        log(Level.WARNING, msg(), t)
    }
}

actual fun logError(t: Throwable) {
    maybe {
        log(Level.SEVERE, t.message, t)
    }
}

private fun maybe(log: Logger.() -> Unit) {
    if (Log.enabled) {
        log(Logger.getGlobal())
    }
}

actual object Log {
    actual var enabled: Boolean = true
}
