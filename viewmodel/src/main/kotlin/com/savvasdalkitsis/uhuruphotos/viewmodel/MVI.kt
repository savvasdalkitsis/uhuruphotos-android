package com.savvasdalkitsis.uhuruphotos.viewmodel

import kotlinx.coroutines.flow.Flow

typealias Handler<S, E, A, M> = (state: S, action: A, effects: suspend (E) -> Unit) -> Flow<M>
typealias Reducer<S, M> = suspend (state: S, mutation: M) -> S
typealias EffectHandler<E> = suspend (effect: E) -> Unit
fun <E> noOp() : EffectHandler<E> = {}
