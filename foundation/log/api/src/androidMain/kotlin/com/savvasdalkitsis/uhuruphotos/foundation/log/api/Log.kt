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

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.michaelflisar.lumberjack.core.L

fun log(tag: String = "", msg: () -> String) {
    tryIgnore {
        if (tag.isNotEmpty()) {
            L.tag(tag).v(msg)
        } else {
            L.v(msg)
        }
    }
}

fun log(t: Throwable) {
    try {
        tempEnable {
            L.w(t)
        }
    } catch (_: Exception) {
        t.printStackTrace()
    }
}

fun log(t: Throwable, msg: () -> String) {
    try {
        tempEnable {
            L.w(t, msg)
        }
    } catch (_: Exception) {
        println("$msg")
        t.printStackTrace()
    }
}

fun logError(t: Throwable) {
    try {
        tempEnable {
            L.e(t)
        }
    }  catch (_: Exception) {
        t.printStackTrace()
    }
}

data object Log {
    var enabled: Boolean
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

suspend fun <V, U> Result<V, Throwable>.andThenTry(transform: suspend (V) -> U): Result<U, Throwable> = andThen {
    runCatchingWithLog { transform(it) }
}

inline fun <T, R> T.runCatchingWithLog(block: T.() -> R): Result<R, Throwable> = try {
    Ok(block())
} catch (e: Throwable) {
    log(e)
    Err(e)
}

private inline fun tempEnable(log: () -> Unit) {
    val old = Log.enabled
    Log.enabled = true
    log()
    Log.enabled = old
}