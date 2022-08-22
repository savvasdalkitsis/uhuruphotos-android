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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

sealed class MediaId<T> private constructor(open val value: T) {

    data class Remote(override val value: String): MediaId<String>(value)
    data class Local(override val value: Long): MediaId<Long>(value)

    companion object {
        operator fun invoke(id: String) = id.toLongOrNull()?.let {
            Local(it)
        } ?: Remote(id)
    }
}
