package com.savvasdalkitsis.uhuruphotos.viewmodel

interface ActionReceiverHost<S: Any, E: Any, A: Any, M : Any> {

    val actionReceiver: ActionReceiver<S, E, A, M>

}