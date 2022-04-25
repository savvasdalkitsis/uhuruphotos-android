package com.savvasdalkitsis.uhuruphotos.viewmodel

import com.savvasdalkitsis.uhuruphotos.log.log
import kotlinx.coroutines.flow.*

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
        handler(_state.value, action) { effect ->
            log("MVI") { "Received side effect to post: $effect from action: $action" }
            _effects.emit(effect)
        }.cancellable().collect { mutation ->
            log("MVI") { "Received mutation $mutation due to action $action" }
            _state.update { reducer(_state.value, mutation) }
        }
    }
}