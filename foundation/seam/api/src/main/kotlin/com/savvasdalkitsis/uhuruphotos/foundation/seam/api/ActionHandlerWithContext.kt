package com.savvasdalkitsis.uhuruphotos.foundation.seam.api

import kotlinx.coroutines.flow.Flow

class ActionHandlerWithContext<C, S, E, A: Action<S, E, M, C>, M: Mutation<S>>(
    private val actionsContext: C,
) : ActionHandler<S, E, A, M> {

    override fun handleAction(
        state: S,
        action: A,
        effect: EffectHandler<E>
    ): Flow<M> = with(actionsContext) {
        action.handle(state, effect)
    }
}