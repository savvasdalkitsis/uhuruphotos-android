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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.model.previewAlbumEmpty
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
internal fun GalleryGroupHeader(
    modifier: Modifier,
    album: Album,
    showSelectionHeader: Boolean,
    showRefreshButton: Boolean,
    onAlbumRefreshClicked: () -> Unit = {},
    onSelectionHeaderClicked: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(visible = showSelectionHeader) {
            ActionIcon(
                onClick = onSelectionHeaderClicked,
                icon = when {
                    album.photos.any { it.selectionMode == MediaItemSelectionMode.UNSELECTED } ->
                        drawable.ic_check_circle
                    else -> drawable.ic_clear
                }
            )
        }
        Column(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                top = 16.dp,
                bottom = 16.dp,
            ).weight(1f),
        ) {
            Text(
                text = album.displayTitle,
                style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
            album.location.takeIf { !it.isNullOrEmpty() }?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = TextStyle.Default.copy(fontWeight = FontWeight.Light)
                )
            }
        }
        if (showRefreshButton) {
            ActionIcon(
                iconModifier = Modifier.alpha(0.6f),
                onClick = onAlbumRefreshClicked,
                icon = drawable.ic_refresh,
            )
        }
    }
}

@Preview
@Composable
internal fun GalleryGroupHeaderPreview() {
    PreviewAppTheme {
        GalleryGroupHeader(
            modifier = Modifier,
            album = previewAlbumEmpty,
            showSelectionHeader = false,
            showRefreshButton = true,
        )
    }
}

@Preview
@Composable
internal fun GalleryGroupPreviewSelection() {
    PreviewAppTheme {
        GalleryGroupHeader(
            modifier = Modifier,
            album = previewAlbumEmpty,
            showSelectionHeader = true,
            showRefreshButton = true,
        )
    }
}