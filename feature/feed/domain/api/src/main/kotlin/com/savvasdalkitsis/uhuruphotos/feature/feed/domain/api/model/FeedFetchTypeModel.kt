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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model

enum class FeedFetchTypeModel(
    val includeMediaWithoutDate: Boolean,
    val includeMediaWithDate: Boolean,
    val onlyVideos: Boolean,
) {
    ALL(
        includeMediaWithoutDate = true,
        includeMediaWithDate = true,
        onlyVideos = false,
    ),
    ONLY_WITH_DATES(
        includeMediaWithoutDate = false,
        includeMediaWithDate = true,
        onlyVideos = false,
    ),
    ONLY_WITHOUT_DATES(
        includeMediaWithoutDate = true,
        includeMediaWithDate = false,
        onlyVideos = false,
    ),
    VIDEOS(
        includeMediaWithoutDate = true,
        includeMediaWithDate = true,
        onlyVideos = true,
    );
}