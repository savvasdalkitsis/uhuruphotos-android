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
package com.savvasdalkitsis.uhuruphotos.api.feed.view

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
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.api.photos.model.SelectionMode
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.AppTheme
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIcon

@Composable
fun AlbumHeader(
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
                    album.photos.any { it.selectionMode == SelectionMode.UNSELECTED } ->
                        R.drawable.ic_check_circle
                    else -> R.drawable.ic_clear
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
                text = album.date,
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
                icon = R.drawable.ic_refresh,
            )
        }
    }
}

@Preview
@Composable
fun AlbumHeaderPreview() {
    PreviewAppTheme {
        AlbumHeader(
            modifier = Modifier,
            album = previewAlbumEmpty,
            showSelectionHeader = false,
            showRefreshButton = true,
        )
    }
}

@Preview
@Composable
fun AlbumHeaderPreviewSelection() {
    PreviewAppTheme {
        AlbumHeader(
            modifier = Modifier,
            album = previewAlbumEmpty,
            showSelectionHeader = true,
            showRefreshButton = true,
        )
    }
}