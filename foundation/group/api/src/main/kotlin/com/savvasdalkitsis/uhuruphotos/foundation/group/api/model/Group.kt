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
package com.savvasdalkitsis.uhuruphotos.foundation.group.api.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Group<K, T>(val items: Map<K, List<T>>)

fun <K, T, N> Group<K, T>.mapValues(mapper: (T) -> N): Group<K, N> = Group(
    items.mapValues { (_, v) -> v.map(mapper) }
)

fun <K, T, N> Group<K, T>.mapNotNullValues(mapper: (T) -> N?): Group<K, N> = Group(
    items.mapValues { (_, v) -> v.mapNotNull(mapper) }
)

inline fun <K, T> Flow<List<T>>.groupBy(
    crossinline id: (T) -> K,
): Flow<Group<K, T>> = map { it.groupBy(id) }.map(::Group)