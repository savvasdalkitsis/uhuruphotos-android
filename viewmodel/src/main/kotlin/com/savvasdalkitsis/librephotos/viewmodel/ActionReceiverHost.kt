package com.savvasdalkitsis.librephotos.viewmodel

import com.savvasdalkitsis.librephotos.log.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ActionReceiverHost<S: Any, E: Any, A: Any, M : Any> : MVIHost<S, E, A, M> {
    val initialState: S
    val actionReceiver: ActionReceiver<S, E, A, M>

    override val state: StateFlow<S> get() = actionReceiver.container.stateFlow
    override val sideEffects: Flow<E> get() = actionReceiver.container.sideEffectFlow

    override fun action(action: A) {
        log(tag = "MVI") { "New action: $action" }
        actionReceiver.action(action)
    }
}