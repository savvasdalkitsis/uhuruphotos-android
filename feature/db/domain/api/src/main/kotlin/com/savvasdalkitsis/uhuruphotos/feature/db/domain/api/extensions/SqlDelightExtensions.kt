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
package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneNotNull
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

suspend fun <R : Any> Query<R>.awaitSingle(): R = withContext(Dispatchers.IO) {
    executeAsOne()
}

suspend fun <R : Any> Query<R>.awaitSingleOrNull(): R? = withContext(Dispatchers.IO) {
    executeAsOneOrNull()
}

suspend fun <R : Any> Query<R>.awaitList(): List<R> = withContext(Dispatchers.IO) {
    executeAsList()
}

suspend fun async(action: () -> Unit) = withContext(Dispatchers.IO) {
    action()
}

suspend fun <T> asyncReturn(action: () -> T) = withContext(Dispatchers.IO) {
    action()
}

suspend fun <T> read(action: () -> T) = withContext(Dispatchers.IO) {
    action()
}

fun <R : Any> Query<R>.asFlowList(): Flow<List<R>> =
    asFlow().mapToList(Dispatchers.IO).distinctUntilChanged()

fun <R : Any> Query<R>.asFlowSet(): Flow<Set<R>> =
    asFlow().mapToList(Dispatchers.IO).map { it.toSet() }.distinctUntilChanged()

fun <R : Any> Query<R>.asFlowSingle(): Flow<R> =
    asFlow().mapToOne(Dispatchers.IO).distinctUntilChanged()

fun <R : Any> Query<R>.asFlowSingleNotNull(): Flow<R> =
    asFlow().mapToOneNotNull(Dispatchers.IO).distinctUntilChanged()

fun <R : Any> Query<R>.asFlowSingleNullable(): Flow<R?> =
    asFlow().mapToOneOrNull(Dispatchers.IO).distinctUntilChanged()