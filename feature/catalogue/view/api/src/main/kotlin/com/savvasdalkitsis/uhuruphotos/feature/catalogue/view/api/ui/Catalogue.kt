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

package com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.view.api.ui.state.CatalogueSortingState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.BackPressHandler
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.AppBarHorizontalPadding
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchField
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_close
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.search_icon

@Composable
fun SharedTransitionScope.Catalogue(
    title: StringResource,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    isEmpty: Boolean,
    emptyContentMessage: StringResource,
    initialFilter: String,
    sorting: CatalogueSortingState,
    onChangeSorting: (CatalogueSortingState) -> Unit,
    onFilterUpdate: (String) -> Unit = {},
    content: LazyGridScope.() -> Unit,
) {
    var showSearch by remember { mutableStateOf(initialFilter.isNotBlank()) }

    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current!!
    val back = remember(showSearch) {
        {
            scope.launch {
                if (showSearch) {
                    showSearch = false
                    onFilterUpdate("")
                } else {
                    navigator.navigateUp()
                }
            }
        }
    }
    BackPressHandler {
        back()
    }
    UhuruScaffold(
        title = { Text(text = stringResource(title)) },
        navigationIcon = { UhuruUpNavButton() },
        actionBarContent = {
            IconButton(onClick = { showSearch = true }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(string.search_icon)
                )
            }
            CatalogueSortingAction(sorting, onChangeSorting)
        },
        alternativeTopBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppBarHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UhuruUpNavButton(
                    icon = {
                        Icon(painter = painterResource(drawable.ic_close), contentDescription = "close")
                    }
                ) {
                    back()
                }

                SearchField(
                    autoFocus = true,
                    showSearchIcon = false,
                    queryCacheKey = "catalogue",
                    latestQuery = initialFilter,
                    onNewQuery = onFilterUpdate,
                    onSearchFor = onFilterUpdate,
                )
            }
        },
        showAlternativeTopBar = showSearch,
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