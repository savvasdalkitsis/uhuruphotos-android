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
package com.savvasdalkitsis.uhuruphotos.implementation.library.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.photos.view.PhotoGridThumbnail
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoGrid
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState

@Composable
internal fun LibraryGrid(
    contentPadding: PaddingValues,
    state: LibraryState,
    action: (LibraryAction) -> Unit
) {
    val auto = stringResource(R.string.auto_albums)
    val user = stringResource(R.string.user_albums)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        columns = GridCells.Fixed(2),
    ) {
        libraryItem(state.autoAlbums, auto) {
            action(LibraryAction.AutoAlbumsSelected)
        }
        libraryItem(state.userAlbums, user) {
            action(LibraryAction.UserAlbumsSelected)
        }
    }
}

internal fun LazyGridScope.libraryItem(
    photoGrid: PhotoGrid?,
    title: String,
    onSelected: () -> Unit,
) {
    photoGrid?.let {
        item(title) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                PhotoGridThumbnail(
                    modifier = Modifier.fillMaxSize(),
                    photoGrid = photoGrid,
                    onSelected = onSelected,
                    shape = RoundedCornerShape(26.dp)
                )
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle1
                        .copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}