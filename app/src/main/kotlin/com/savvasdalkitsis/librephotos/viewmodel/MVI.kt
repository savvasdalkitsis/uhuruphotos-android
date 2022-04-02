package com.savvasdalkitsis.librephotos.viewmodel

import kotlinx.coroutines.flow.Flow

typealias Handler<S, E, A, M> = (S, A, suspend (E) -> Unit) -> Flow<M>
typealias Reducer<S, M> = (S, M) -> S
typealias EffectHandler<E> = (E) -> Unit
fun <E> noOp() : EffectHandler<E> = {}
