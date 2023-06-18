/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.result.api

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.runCatching

typealias SimpleResult = Result<Unit, Throwable>

val simpleOk = Ok(Unit).simple()

fun <V> Result<V, Throwable>.simple() : SimpleResult = mapEither(
    success = { },
    failure = { it },
)

suspend fun <V, N, E : Throwable> Result<V, E>.mapCatching(block: suspend V.() -> N): Result<N, Throwable> = andThen {
    runCatching {
        block(it)
    }
}