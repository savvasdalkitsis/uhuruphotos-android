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
package com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model

enum class MediaSource(val toArgument: String) {

    REMOTE("remote"),
    LOCAL("local");

    companion object {
        fun fromArgument(argument: String?): MediaSource = when (argument) {
            "local" -> LOCAL
            "remote" -> REMOTE
            else -> throw IllegalArgumentException("Unknown media image source: $argument")
        }

        fun fromUrl(url: String?): MediaSource = when {
            url.orEmpty().startsWith("content://") -> LOCAL
            else -> REMOTE
        }
    }

}
