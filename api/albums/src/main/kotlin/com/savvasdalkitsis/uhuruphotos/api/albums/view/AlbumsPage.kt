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
package com.savvasdalkitsis.uhuruphotos.api.albums.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.savvasdalkitsis.uhuruphotos.api.albums.view.state.AlbumSorting
import com.savvasdalkitsis.uhuruphotos.api.ui.view.BackNavButton
import com.savvasdalkitsis.uhuruphotos.api.ui.view.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.api.ui.view.NoContent

@Composable
fun AlbumsPage(
    title: Int,
    onRefresh: () -> Unit,
    onBackPressed: () -> Unit,
    isRefreshing: Boolean,
    isEmpty: Boolean,
    emptyContentMessage: Int,
    sorting: AlbumSorting,
    onChangeSorting: (AlbumSorting) -> Unit,
    content: LazyGridScope.() -> Unit,
) {
    CommonScaffold(
        title = { Text(text = stringResource(title)) },
        navigationIcon = {
            BackNavButton {
                onBackPressed()
            }
        },
        actionBarContent = {
            AlbumsSortingAction(sorting, onChangeSorting)
        }
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
            onRefresh = onRefresh,
        ) {
            when {
                !isRefreshing && isEmpty -> NoContent(emptyContentMessage)
                else -> LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(160.dp),
                    contentPadding = contentPadding,
                ) {
                    content(this)
                }
            }
        }
    }
}