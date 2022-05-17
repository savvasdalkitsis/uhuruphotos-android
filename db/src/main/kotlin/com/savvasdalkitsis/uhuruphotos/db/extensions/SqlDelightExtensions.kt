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
package com.savvasdalkitsis.uhuruphotos.db.extensions

import com.squareup.sqldelight.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <R : Any> Query<R>.awaitSingle(): R = withContext(Dispatchers.IO) {
    executeAsOne()
}

suspend fun <R : Any> Query<R>.awaitSingleOrNull(): R? = withContext(Dispatchers.IO) {
    executeAsOneOrNull()
}

suspend fun <R : Any> Query<R>.await(): List<R> = withContext(Dispatchers.IO) {
    executeAsList()
}

suspend fun async(action: () -> Unit) = withContext(Dispatchers.IO) {
    action()
}

suspend fun <T> read(action: () -> T) = withContext(Dispatchers.IO) {
    action()
}
