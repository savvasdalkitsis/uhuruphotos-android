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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem

data class MediaGridState(
    val mediaItem1: MediaItem? = null,
    val mediaItem2: MediaItem? = null,
    val mediaItem3: MediaItem? = null,
    val mediaItem4: MediaItem? = null,
) {
    val hasMoreThanOneItem = mediaItem2 != null

    companion object {
        operator fun invoke(mediaItems: List<MediaItem?>)= MediaGridState(
            mediaItems.getOrNull(0),
            mediaItems.getOrNull(1),
            mediaItems.getOrNull(2),
            mediaItems.getOrNull(3),
        )
    }
}