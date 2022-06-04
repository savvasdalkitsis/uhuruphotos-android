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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.api.ui.view.NoContent
import com.savvasdalkitsis.uhuruphotos.implementation.library.seam.LibraryAction
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.implementation.library.view.state.LibraryState

@Composable
internal fun LibraryPage(
    contentPadding: PaddingValues,
    action: (LibraryAction) -> Unit,
    isRefreshing: Boolean,
    refreshAction: LibraryAction,
    isEmpty: Boolean,
    emptyContentMessage: Int,
    headerTitle: Int,
    sorting: AlbumSorting,
    changeSorting: (AlbumSorting) -> LibraryAction,
    content: LazyGridScope.() -> Unit,
) {
    SwipeRefresh(
        indicatorPadding = contentPadding,
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = { action(refreshAction) }
    ) {
        when {
            !isRefreshing && isEmpty -> NoContent(emptyContentMessage)
            else -> LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(160.dp),
                contentPadding = contentPadding,
            ) {
                item("header", span = { GridItemSpan(maxCurrentLineSpan) }) {
                    AlbumsHeader(
                        sorting,
                        stringResource(headerTitle),
                    ) { newSorting ->
                        action(changeSorting(newSorting))
                    }
                }
                content(this)
            }
        }
    }
}