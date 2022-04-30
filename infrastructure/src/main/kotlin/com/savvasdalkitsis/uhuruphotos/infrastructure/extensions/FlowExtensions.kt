package com.savvasdalkitsis.uhuruphotos.infrastructure.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.log.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
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
            CoroutineScope(Dispatchers.IO).launch {
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
    CoroutineScope(Dispatchers.IO).launch {
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