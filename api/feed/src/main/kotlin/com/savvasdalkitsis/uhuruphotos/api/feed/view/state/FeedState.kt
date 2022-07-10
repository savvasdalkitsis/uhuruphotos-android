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
package com.savvasdalkitsis.uhuruphotos.api.feed.view.state

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album

data class FeedState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val albums: List<Album> = emptyList(),
    val feedDisplay: FeedDisplay = FeedDisplays.default,
) {
    val hasPhotos get() = albums.sumOf { it.photos.size } > 0

    override fun toString(): String {
        return "FeedState(isLoading=$isLoading, isEmpty=$isEmpty, albumsSize=${albums.size}, feedDisplay=$feedDisplay)"
    }

}