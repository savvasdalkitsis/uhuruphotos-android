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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSorting
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton

@Composable
fun Catalogue(
    @StringRes title: Int,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    isEmpty: Boolean,
    emptyContentMessage: Int,
    sorting: CatalogueSorting,
    onChangeSorting: (CatalogueSorting) -> Unit,
    content: LazyGridScope.() -> Unit,
) {
    CommonScaffold(
        title = { Text(text = stringResource(title)) },
        navigationIcon = { UpNavButton() },
        actionBarContent = {
            CatalogueSortingAction(sorting, onChangeSorting)
        }
    ) { contentPadding ->
        SwipeRefresh(
            indicatorPadding = contentPadding,
            isRefreshing = isRefreshing,
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