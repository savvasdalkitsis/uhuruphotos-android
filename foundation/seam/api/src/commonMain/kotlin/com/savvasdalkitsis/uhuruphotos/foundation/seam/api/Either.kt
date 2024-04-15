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

sealed class Either<A, B> {

    data class Left<A, B>(val value: A) : Either<A, B>()
    data class Right<A, B>(val value: B) : Either<A, B>()

    fun <AA> mapLeft(mapper: (A) -> AA): Either<AA, B> = when (this) {
        is Left -> Left(mapper(value))
        is Right -> Right(value)
    }

    fun <BB> mapRight(mapper: (B) -> BB): Either<A, BB> = when (this) {
        is Left -> Left(value)
        is Right -> Right(mapper(value))
    }
}