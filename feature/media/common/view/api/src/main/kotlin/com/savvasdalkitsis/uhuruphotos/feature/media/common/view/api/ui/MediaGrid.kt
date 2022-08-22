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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.MediaGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.CustomColors

@Composable
fun MediaGrid(
    modifier: Modifier = Modifier,
    state: MediaGridState,
    onSelected: () -> Unit,
    shape: Shape = RectangleShape,
) {
    Column(
        modifier = modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .clip(shape)
            .clickable(
                onClick = onSelected,
            ),
    ) {
        Row {
            GridItem(state.mediaItem1)
            GridItem(state.mediaItem2)
        }
        Row {
            GridItem(state.mediaItem3)
            GridItem(state.mediaItem4)
        }
    }
}

@Composable
private fun RowScope.GridItem(mediaItem: MediaItem?) {
    if (mediaItem != null) {
        MediaItemThumbnail(
            modifier = Modifier
                .weight(1f),
            mediaItem = mediaItem,
            onItemSelected = { _, _, _ -> },
            aspectRatio = 1f,
            contentScale = ContentScale.Crop,
            itemPadding = 0.dp,
            miniIcons = true,
            selectable = false
        )
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .background(CustomColors.emptyItem)
        )
    }
}
