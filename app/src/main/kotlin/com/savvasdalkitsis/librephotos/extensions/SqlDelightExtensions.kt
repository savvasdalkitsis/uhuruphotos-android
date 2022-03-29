package com.savvasdalkitsis.librephotos.extensions

import com.squareup.sqldelight.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

suspend fun crud(action: () -> Unit) = withContext(Dispatchers.IO) {
    action()
}
