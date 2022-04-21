package com.savvasdalkitsis.librephotos.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MVIHost<S: Any, E: Any, A: Any, M : Any> {

    val state: StateFlow<S>
    val sideEffects: Flow<E>
    fun action(action: A)
}