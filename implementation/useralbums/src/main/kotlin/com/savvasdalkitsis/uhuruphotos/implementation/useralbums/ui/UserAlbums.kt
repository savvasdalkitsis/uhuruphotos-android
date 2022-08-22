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

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.Catalogue
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsAction.Refresh
import com.savvasdalkitsis.uhuruphotos.implementation.useralbums.seam.UserAlbumsState

@Composable
internal fun UserAlbums(
    state: UserAlbumsState,
    action: (UserAlbumsAction) -> Unit,
) {
    Catalogue(
        title = string.user_albums,
        onBackPressed = { action(NavigateBack) },
        onRefresh = { action(Refresh) },
        isRefreshing = state.isLoading,
        isEmpty = state.albums.isEmpty(),
        emptyContentMessage = string.no_user_albums,
        sorting = state.sorting,
        onChangeSorting = { action(ChangeSorting(it)) },
    ) {
        state.albums.forEach { album ->
            item(album.id) {
                UserAlbumItem(album, action)
            }
        }
    }
}