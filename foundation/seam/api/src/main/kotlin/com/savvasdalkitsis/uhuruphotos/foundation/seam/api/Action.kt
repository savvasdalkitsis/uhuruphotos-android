package com.savvasdalkitsis.uhuruphotos.foundation.seam.api

import kotlinx.coroutines.flow.Flow

fun interface Action<S, E, M, C> {

    context(C) fun handle(
        state: S,
        effect: EffectHandler<E>
    ): Flow<M>
}