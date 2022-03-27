package com.savvasdalkitsis.librephotos.extensions

import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var windowStartTime = System.currentTimeMillis()
    var emitted = false
    collect { value ->
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - windowStartTime
        if (delta >= windowDuration) {
            windowStartTime += delta / windowDuration * windowDuration
            emitted = false
        }
        if (!emitted) {
            emit(value)
            emitted = true
        }
    }
}

inline fun <reified T> List<Flow<T>>.flattenFlowList(): Flow<List<T>> = flow {
    forEach { flow ->
        flow.collect {
            emit(listOf(it))
        }
    }
}
data class Group<K, T>(val items: Map<K, List<T>>)

inline fun <K, T> Flow<List<T>>.groupBy(
    crossinline id: (T) -> K,
): Flow<Group<K, T>> = map { it.groupBy(id) }.map(::Group)
