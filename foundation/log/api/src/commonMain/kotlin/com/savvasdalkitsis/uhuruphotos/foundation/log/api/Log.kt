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

expect fun log(tag: String = "", msg: () -> String)

expect fun log(t: Throwable)

expect fun log(t: Throwable, msg: () -> String)

expect fun logError(t: Throwable)

expect object Log {
    var enabled: Boolean
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