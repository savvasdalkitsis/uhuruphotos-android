/*
Copyright 2024 Savvas Dalkitsis

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

package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.ui.UserAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.AddSelectedCelsToAlbum
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.AskForNewAlbumName
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FilterAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.HideAddToAlbumDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.UhuruIconOutlineButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchField
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.sheet.SheetHandle
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_photo_album
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.add_to_album
import uhuruphotos_android.foundation.strings.api.generated.resources.create_album

@Composable
internal fun SharedTransitionScope.AddToAlbumDialog(
    albums: ImmutableList<UserAlbumState>,
    action: (FeedAction) -> Unit,
) {
    BottomSheetDialog(
        onDismissRequest = {
            action(HideAddToAlbumDialog)
        },
        properties = BottomSheetDialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            dismissWithAnimation = true,
            enableEdgeToEdge = true,
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SheetHandle()
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = stringResource(string.add_to_album),
                    style = MaterialTheme.typography.headlineMedium,
                )
                SearchField(
                    queryCacheKey = "albums",
                    latestQuery = "",
                    showSearchIcon = false,
                    onNewQuery = { action(FilterAlbums(it)) },
                    onSearchFor = { action(FilterAlbums(it)) },
                )
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding =
                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues() +
                        WindowInsets.ime.only(WindowInsetsSides.Bottom).asPaddingValues(),
                ) {
                    createNewButtonItem(action)
                    for (album in albums) {
                        if (album.visible) {
                            item(album.id) {
                                UserAlbumItem(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    album = album,
                                ) {
                                    action(AddSelectedCelsToAlbum(album.id))
                                    action(HideAddToAlbumDialog)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun LazyGridScope.createNewButtonItem(action: (FeedAction) -> Unit) {
    item("create_new", span = { GridItemSpan(maxLineSpan) }) {
        UhuruIconOutlineButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            icon = drawable.ic_photo_album,
            text = stringResource(string.create_album),
        ) {
            action(AskForNewAlbumName)
        }
    }
}