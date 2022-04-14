package com.savvasdalkitsis.librephotos.viewmodel

import com.orhanobut.logger.Logger
import com.savvasdalkitsis.librephotos.infrastructure.log.log
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce

class ActionReceiver<S : Any, E : Any, A : Any, M : Any>(
    private val handler: Handler<S, E, A, M>,
    private val reducer: Reducer<S, M>,
    override val container: Container<S, E>,
) : ContainerHost<S, E> {

    fun action(action: A) = intent {
        log(tag = "MVI") { "Starting handling of action $action" }
        handler(state, action) { effect ->
            log(tag = "MVI") { "Received side effect to post: $effect" }
            postSideEffect(effect)
        }.collect { mutation ->
            Logger.log(Logger.VERBOSE, "MVI", "Received mutation $mutation", null)
            reduce { reducer(state, mutation) }
        }
    }
}