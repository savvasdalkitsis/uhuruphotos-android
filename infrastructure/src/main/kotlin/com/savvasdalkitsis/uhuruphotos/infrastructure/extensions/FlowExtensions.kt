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
package com.savvasdalkitsis.uhuruphotos.infrastructure.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.log.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

data class Group<K, T>(val items: Map<K, List<T>>)

fun <K, T, N> Group<K, T>.mapValues(mapper: (T) -> N): Group<K, N> = Group(
    items.mapValues { (_,v) -> v.map(mapper) }
)

inline fun <K, T> Flow<List<T>>.groupBy(
    crossinline id: (T) -> K,
): Flow<Group<K, T>> = map { it.groupBy(id) }.map(::Group)

fun <T> Flow<T>.safelyOnStart(
    block: suspend () -> Unit
): Flow<Result<T, Throwable>> =
    map<T, Result<T, Throwable>>(::Ok)
        .onStart {
            CoroutineScope(currentCoroutineContext() + Dispatchers.IO).launch {
                try {
                    block()
                } catch (e: IOException) {
                    log(e)
                    emit(Err(e))
                }
            }
        }

fun <T> Flow<T>.safelyOnStartIgnoring(
    block: suspend () -> Unit
): Flow<T> = onStart {
    CoroutineScope(currentCoroutineContext() + Dispatchers.IO).launch {
        try {
            block()
        } catch (e: IOException) {
            log(e)
        }
    }
}

fun <V> Flow<Result<V, Throwable>>.onErrors(onError: suspend (Throwable) -> Unit) : Flow<V> = mapNotNull {
    when (it) {
        is Ok -> it.value
        is Err -> {
            onError(it.error)
            null
        }
    }
}

fun <V> Flow<Result<V, Throwable>>.onErrorsEmit(onError: suspend (Throwable) -> V) : Flow<V> = mapNotNull {
    when (it) {
        is Ok -> it.value
        is Err -> {
            onError(it.error)
        }
    }
}

fun <V> Flow<Result<V, Throwable>>.onErrorsIgnore() : Flow<V> = onErrors { }
