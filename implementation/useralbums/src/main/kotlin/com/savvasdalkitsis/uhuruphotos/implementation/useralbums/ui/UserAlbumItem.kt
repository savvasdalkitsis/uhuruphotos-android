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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbums.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.useralbums.ui.state.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaGrid
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaItemThumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.UserAlbumSelected

@Composable
internal fun UserAlbumItem(
    album: UserAlbum,
    action: (UserAlbumsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { action(UserAlbumSelected(album)) }
    ) {
        if (album.cover.hasMoreThanOneItem) {
            MediaGrid(
                state = album.cover,
                onSelected = {
                    action(UserAlbumSelected(album))
                },
                shape = RoundedCornerShape(26.dp),
            )
        } else {
            MediaItemThumbnail(
                mediaItem = album.cover.mediaItem1 ?: MediaItem(MediaId.Remote(""), ""),
                onItemSelected = { _, _, _ ->
                    action(UserAlbumSelected(album))
                },
                aspectRatio = 1f,
                contentScale = ContentScale.Crop,
                shape = RoundedCornerShape(26.dp),
            )
        }
        Text(
            text = album.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1
                .copy(fontWeight = FontWeight.Bold),
        )
        album.photoCount?.let {
            Text(
                text = pluralStringResource(R.plurals.item_count, it, it),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}