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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.ui

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
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsAction.AutoAlbumSelected
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.MediaItemThumbnail
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R

@Composable
internal fun AutoAlbumItem(
    album: AutoAlbum,
    action: (AutoAlbumsAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { action(AutoAlbumSelected(album)) }
    ) {
        MediaItemThumbnail(
            mediaItem = album.cover,
            onItemSelected = { _, _, _ ->
                action(AutoAlbumSelected(album))
            },
            contentScale = ContentScale.Crop,
            shape = RoundedCornerShape(26.dp),
        )
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