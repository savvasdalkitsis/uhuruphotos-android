/*
Copyright 2022 Savvas Dalkitsis

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

import com.michaelflisar.lumberjack.L

inline fun log(tag: String = "", msg: () -> String) {
    if (tag.isNotEmpty()) {
        L.tag(tag).v(msg)
    } else {
        L.v(msg)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(t: Throwable) {
    L.w(t)
}

@Suppress("NOTHING_TO_INLINE")
inline fun logError(t: Throwable) {
    L.e(t)
}

object Log {
    var enabled: Boolean
        get() = L.enabled
        set(value) {
            L.enabled = value
        }
}

inline fun <T, R : Any> T.runCatchingWithLog(block: T.() -> R): Result<R> = try {
    Result.success(block())
} catch (e: Throwable) {
    log(e)
    Result.failure(e)
}
