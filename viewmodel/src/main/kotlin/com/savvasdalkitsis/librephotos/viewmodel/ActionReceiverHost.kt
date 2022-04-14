package com.savvasdalkitsis.librephotos.viewmodel

interface ActionReceiverHost<S: Any, E: Any, A: Any, M : Any> {
    val initialState: S
    val actionReceiver: ActionReceiver<S, E, A, M>
}