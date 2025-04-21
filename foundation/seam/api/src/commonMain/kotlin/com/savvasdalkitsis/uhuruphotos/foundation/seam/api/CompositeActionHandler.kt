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
package com.savvasdalkitsis.uhuruphotos.foundation.seam.api

import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CompositeActionHandler<
        S1: Any, A1: Any,
        S2: Any, A2: Any,
>(
    private val handler1: ActionHandler<S1, A1>,
    private val handler2: ActionHandler<S2, A2>,
) : ActionHandler<Pair<S1, S2>, Either<A1, A2>> {

    override fun handleAction(
        state: Pair<S1, S2>,
        action: Either<A1, A2>
    ): Flow<Mutation<Pair<S1, S2>>> = when(val flows = action
        .mapLeft {
            handler1.handleAction(state.first, it)
        }.mapRight {
            handler2.handleAction(state.second, it)
        }) {
            is Left -> flows.value.map { m ->
                object : Mutation<Pair<S1, S2>> {
                    override fun reduce(state: Pair<S1, S2>): Pair<S1, S2> {
                        return m.reduce(state.first) to state.second
                    }
                    override fun toString() = m.toString()
                }
            }
            is Right -> flows.value.map { m ->
                object : Mutation<Pair<S1, S2>> {
                    override fun reduce(state: Pair<S1, S2>): Pair<S1, S2> {
                        return state.first to m.reduce(state.second)
                    }
                    override fun toString() = m.toString()
                }
            }
        }
}