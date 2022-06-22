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
package com.savvasdalkitsis.uhuruphotos.api.seam

import com.savvasdalkitsis.uhuruphotos.api.log.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SeamViaHandler<S : Any, E : Any, A : Any, M : Mutation<S>>(
    private val handler: ActionHandler<S, E, A, M>,
    initialState: S,
) : Seam<S, E, A, M> {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<S> = _state
    private val _effects = MutableSharedFlow<E>(replay = 0)
    override val effects: Flow<E> = _effects
    private val mainThreadMutex = Mutex(locked = false)

    override suspend fun action(action: A)  {
        log("MVI") { "Starting handling of action $action" }
        handler.handleAction(
            _state.value,
            action
        ) { effect ->
            log("MVI") { "Received side effect to post: $effect from action: $action" }
            _effects.emit(effect)
        }.flowOn(Dispatchers.Default)
            .cancellable()
            .map { mutation ->
                mainThreadMutex.withLock {
                    log("MVI") { "Received mutation $mutation due to action $action" }
                    mutation.reduce(_state.value)
                }
            }
            .distinctUntilChanged()
            .collect { newState ->
                _state.update { newState }
                log("MVI") { "State updated to: ${_state.value}" }
            }
    }

    companion object {
        fun <S : Any, E : Any, A : Any, M : Mutation<S>> handler(
            handler: ActionHandler<S, E, A, M>,
            initialState: S,
        ) = SeamViaHandler(handler, initialState)
    }
}