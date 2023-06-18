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
package com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.runCatching
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

fun <T> Flow<T>.safelyOnStartIgnoring(
    block: suspend () -> Unit
): Flow<T> = onStart {
    CoroutineScope(currentCoroutineContext() + Dispatchers.IO).launch {
        try {
            block()
        } catch (e: Exception) {
            log(e)
        }
    }
}

fun <T> Flow<T>.safelyOnStart(
    block: suspend () -> Unit
): Flow<Result<T, Throwable>> = onStartWithResult {
    runCatching { block() }
}

fun <T> Flow<T>.onStartWithResult(
    block: suspend () -> SimpleResult
): Flow<Result<T, Throwable>> = channelFlow {
    map { Ok(it) }
        .onStart {
            CoroutineScope(currentCoroutineContext() + Dispatchers.IO).launch {
                val result = block()
                if (result is Err) {
                    send(Err(result.error))
                }
            }
        }
        .collect { send(it) }
}

fun <V> Flow<Result<V, Throwable>>.onErrors(onError: suspend (Throwable) -> Unit) : Flow<V> =
    mapNotNull {
        it.getOrElse { e ->
            onError(e)
            null
        }
    }

fun <V> Flow<Result<V, Throwable>>.onErrorsIgnore() : Flow<V> = onErrors { }
