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
package com.savvasdalkitsis.uhuruphotos.library.view

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.api.home.view.HomeScaffold
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.library.seam.LibraryAction.RefreshAutoAlbums
import com.savvasdalkitsis.uhuruphotos.library.view.state.LibraryState
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.ui.view.NoContent

@Composable
fun Library(
    state: LibraryState,
    homeFeedDisplay: FeedDisplay,
    action: (LibraryAction) -> Unit,
    controllersProvider: ControllersProvider,
) {
    HomeScaffold(
        modifier = Modifier,
        showLibrary = true,
        navController = controllersProvider.navController!!,
        userInformationState = null,
        homeFeedDisplay = homeFeedDisplay,
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = state.isLoading),
            onRefresh = { action(RefreshAutoAlbums) }
        ) {
            when {
                !state.isLoading && state.autoAlbums.isEmpty() -> NoContent(R.string.no_auto_albums)
                else -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    contentPadding = contentPadding,
                ) {
                    item("header", span = { GridItemSpan(maxCurrentLineSpan) }) {
                        AutoAlbumsHeader(state, action)
                    }
                    state.autoAlbums.forEach { album ->
                        item(album.id) {
                            AutoAlbumItem(album, action)
                        }
                    }
                }
            }
        }
    }
}