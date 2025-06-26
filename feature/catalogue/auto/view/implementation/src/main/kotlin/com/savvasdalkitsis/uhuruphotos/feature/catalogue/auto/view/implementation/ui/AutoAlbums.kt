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

package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.api.ui.AutoAlbumItem
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.AutoAlbumsState
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.AutoAlbumSelected
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.AutoAlbumsAction
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.ChangeSorting
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.FilterAlbums
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.view.implementation.seam.actions.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.Catalogue
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.auto_generated_albums
import uhuruphotos_android.foundation.strings.api.generated.resources.no_auto_albums

@Composable
internal fun SharedTransitionScope.AutoAlbums(
    state: AutoAlbumsState,
    action: (AutoAlbumsAction) -> Unit,
) {
    Catalogue(
        title = string.auto_generated_albums,
        onRefresh = { action(Refresh) },
        isRefreshing = state.isLoading,
        isEmpty = state.albums.isEmpty(),
        emptyContentMessage = string.no_auto_albums,
        sorting = state.sorting,
        initialFilter = state.filter,
        onFilterUpdate =  { action(FilterAlbums(it)) },
        onChangeSorting = { action(ChangeSorting(it)) },
    ) {
        state.albums
            .filter { it.visible }
            .forEach { album ->
                item(album.id) {
                    AutoAlbumItem(
                        modifier = Modifier
                            .animateItem()
                            .padding(8.dp),
                        album = album,
                        onAlbumSelected = { action(AutoAlbumSelected(album))},
                    )
                }
            }
    }
}