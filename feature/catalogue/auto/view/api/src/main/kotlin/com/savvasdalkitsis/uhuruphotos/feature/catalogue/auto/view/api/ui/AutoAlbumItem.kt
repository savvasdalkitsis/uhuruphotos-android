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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.state.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.Cel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import org.jetbrains.compose.resources.pluralStringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.plurals
import uhuruphotos_android.foundation.strings.api.generated.resources.item_count

@Composable
fun SharedTransitionScope.AutoAlbumItem(
    modifier: Modifier = Modifier,
    album: AutoAlbum,
    shape: Shape = MaterialTheme.shapes.large,
    miniIcons: Boolean = false,
    onAlbumSelected: (AutoAlbum) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onAlbumSelected(album) }
    ) {
        Cel(
            state = album.cover.toCel(),
            onSelected = { onAlbumSelected(album) },
            contentScale = ContentScale.Crop,
            miniIcons = miniIcons,
            shape = shape,
        )
        Text(
            text = album.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
                .copy(fontWeight = FontWeight.Bold),
        )
        album.photoCount?.let {
            Text(
                text = pluralStringResource(plurals.item_count, it, it),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}