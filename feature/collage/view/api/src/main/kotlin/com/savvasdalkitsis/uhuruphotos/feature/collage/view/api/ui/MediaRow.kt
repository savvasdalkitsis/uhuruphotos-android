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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.MediaRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.MediaRowSlot.MediaSlot
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaItemSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaItemThumbnail

@Composable
internal fun MediaRow(
    modifier: Modifier = Modifier,
    maintainAspectRatio: Boolean = true,
    miniIcons: Boolean = false,
    onMediaItemSelected: MediaItemSelected,
    onMediaItemLongPressed: (MediaItem) -> Unit,
    vararg slots: MediaRowSlot
) {
    Row(modifier = modifier) {
        for (item in slots) {
            when (item) {
                is MediaSlot -> {
                    val aspectRatio = when {
                        maintainAspectRatio -> item.mediaItem.ratio
                        else -> 1f
                    }
                    MediaItemThumbnail(
                        modifier = Modifier
                            .weight(aspectRatio),
                        mediaItem = item.mediaItem,
                        onItemSelected = onMediaItemSelected,
                        aspectRatio = aspectRatio,
                        contentScale = when {
                            maintainAspectRatio -> ContentScale.FillBounds
                            else -> ContentScale.Crop
                        },
                        miniIcons = miniIcons,
                        onLongClick = onMediaItemLongPressed,
                    )
                }
                EmptySlot -> Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}