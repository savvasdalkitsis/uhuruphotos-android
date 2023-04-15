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

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.AutoAlbumsAction
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.Catalogue
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun AutoAlbums(
    state: AutoAlbumsState,
    action: (AutoAlbumsAction) -> Unit,
) {
    Catalogue(
        title = string.auto_generated_albums,
        onBackPressed = { action(NavigateBack) },
        onRefresh = { action(Refresh) },
        isRefreshing = state.isLoading,
        isEmpty = state.albums.isEmpty(),
        emptyContentMessage = string.no_auto_albums,
        sorting = state.sorting,
        onChangeSorting = { action(ChangeSorting(it)) },
    ) {
        state.albums.forEach { album ->
            item(album.id) {
                AutoAlbumItem(album, action)
            }
        }
    }
}