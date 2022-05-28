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
package com.savvasdalkitsis.uhuruphotos.viewmodel

import com.savvasdalkitsis.uhuruphotos.api.log.log
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update

class ActionReceiver<S : Any, E : Any, A : Any, M : Any>(
    private val handler: Handler<S, E, A, M>,
    private val reducer: Reducer<S, M>,
    initialState: S,
) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state
    private val _effects = MutableSharedFlow<E>(replay = 0)
    val effects: Flow<E> = _effects

    suspend fun action(action: A)  {
        log("MVI") { "Starting handling of action $action" }
        handler(
            _state.value,
            action
        ) { effect ->
            log("MVI") { "Received side effect to post: $effect from action: $action" }
            _effects.emit(effect)
        }.flowOn(Default)
            .cancellable()
            .collect { mutation ->
                log("MVI") { "Received mutation $mutation due to action $action" }
                _state.update { reducer(_state.value, mutation) }
            }
    }
}