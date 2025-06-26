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

package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.ui.UserAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.UserAlbumsState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.FilterAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.UserAlbumSelected
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.implementation.seam.action.UserAlbumsAction
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.Catalogue
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.no_user_albums
import uhuruphotos_android.foundation.strings.api.generated.resources.user_albums

@Composable
internal fun SharedTransitionScope.UserAlbums(
    state: UserAlbumsState,
    action: (UserAlbumsAction) -> Unit,
) {
    Catalogue(
        title = string.user_albums,
        onRefresh = { action(Refresh) },
        isRefreshing = state.isLoading,
        isEmpty = state.albums.isEmpty(),
        emptyContentMessage = string.no_user_albums,
        sorting = state.sorting,
        initialFilter = state.filter,
        onFilterUpdate =  { action(FilterAlbums(it)) },
        onChangeSorting = { action(ChangeSorting(it)) },
    ) {
        state.albums
            .filter { it.visible }
            .forEach { album ->
                item(album.id) {
                    UserAlbumItem(
                        modifier = Modifier.animateItem()
                            .padding(8.dp),
                        album = album,
                        onAlbumSelected = { action(UserAlbumSelected(album)) },
                    )
                }
            }
    }
}