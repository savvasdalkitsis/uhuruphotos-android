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

class CompositeEffectHandler<E1, E2>(
    private val effectHandler1: EffectHandler<E1>,
    private val effectHandler2: EffectHandler<E2>,
) : EffectHandler<Either<E1, E2>> {
    override suspend fun handleEffect(effect: Either<E1, E2>) {
        when (effect) {
            is Left -> effectHandler1.handleEffect(effect.value)
            is Right -> effectHandler2.handleEffect(effect.value)
        }
    }
}