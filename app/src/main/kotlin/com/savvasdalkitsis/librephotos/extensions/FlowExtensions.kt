package com.savvasdalkitsis.librephotos.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Group<K, T>(val items: Map<K, List<T>>)

inline fun <K, T> Flow<List<T>>.groupBy(
    crossinline id: (T) -> K,
): Flow<Group<K, T>> = map { it.groupBy(id) }.map(::Group)
