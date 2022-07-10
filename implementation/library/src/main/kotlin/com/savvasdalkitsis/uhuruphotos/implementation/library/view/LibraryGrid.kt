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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoGrid
import com.savvasdalkitsis.uhuruphotos.api.photos.view.PhotoGridThumbnail
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.CustomColors
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.AutoAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.FavouritePhotosSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.HiddenPhotosSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.TrashSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction.UserAlbumsSelected
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.api.icons.R as Icons

@Composable
internal fun LibraryGrid(
    contentPadding: PaddingValues,
    state: LibraryState,
    action: (LibraryAction) -> Unit
) {
    val auto = stringResource(R.string.auto_albums)
    val user = stringResource(R.string.user_albums)
    val favourites = stringResource(R.string.favourite_photos)
    val hidden = stringResource(R.string.hidden_photos)
    val trash = stringResource(R.string.trash)
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        columns = GridCells.Adaptive(160.dp),
    ) {
        pillItem(trash, Icons.drawable.ic_delete, { GridItemSpan(maxCurrentLineSpan / 2) }) {
            action(TrashSelected)
        }
        pillItem(hidden, Icons.drawable.ic_invisible, { GridItemSpan(maxCurrentLineSpan) }) {
            action(HiddenPhotosSelected)
        }

        libraryItem(state.autoAlbums, auto) {
            action(AutoAlbumsSelected)
        }
        libraryItem(state.userAlbums, user) {
            action(UserAlbumsSelected)
        }
        libraryItem(state.favouritePhotos, favourites) {
            action(FavouritePhotosSelected)
        }
    }
}

internal fun LazyGridScope.pillItem(
    title: String,
    @DrawableRes icon: Int,
    span: (LazyGridItemSpanScope.() -> GridItemSpan)? = null,
    onSelected: () -> Unit,
) {
    item(title, span) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .clickable { onSelected() }
                    .background(CustomColors.emptyItem)
                    .padding(16.dp),
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    painter = painterResource(icon),
                    contentDescription = null
                )
            }
            Subtitle(title)
        }
    }
}

internal fun LazyGridScope.libraryItem(
    photoGrid: PhotoGrid?,
    title: String,
    @DrawableRes overlayIcon: Int? = null,
    onSelected: () -> Unit,
) {
    photoGrid?.let {
        item(title) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PhotoGridThumbnail(
                        modifier = Modifier.fillMaxSize(),
                        photoGrid = photoGrid,
                        onSelected = onSelected,
                        shape = RoundedCornerShape(26.dp)
                    )
                    if (overlayIcon != null) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center),
                            painter = painterResource(overlayIcon),
                            contentDescription = null
                        )
                    }
                }
                Subtitle(title)
            }
        }
    }
}

@Composable
private fun ColumnScope.Subtitle(title: String) {
    Text(
        modifier = Modifier
            .align(CenterHorizontally)
            .padding(4.dp),
        text = title,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.subtitle1
            .copy(fontWeight = FontWeight.Bold),
    )
}