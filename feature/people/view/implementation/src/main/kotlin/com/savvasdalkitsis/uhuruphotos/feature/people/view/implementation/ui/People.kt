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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PersonThumbnail
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.PeopleAction
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions.ToggleSortOrder
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder.ASCENDING
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder.DESCENDING
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton

@Composable
fun People(
    state: PeopleState,
    action: (PeopleAction) -> Unit,
) {
    CommonScaffold(
        title = { Text("People") },
        navigationIcon = { UpNavButton() },
        actionBarContent = {
            ActionIcon(onClick = { action(ToggleSortOrder) }, icon = when (state.sortOrder) {
                ASCENDING -> drawable.ic_sort_az_ascending
                DESCENDING -> drawable.ic_sort_az_descending
            })
        }
    ) { contentPadding ->
        if (state.people.isEmpty()) {
            FullLoading()
        } else {
            val columns = when (LocalWindowSize.current.widthSizeClass) {
                Compact -> 4
                Medium -> 6
                else -> 9
            }
            SwipeRefresh(
                indicatorPadding = contentPadding,
                isRefreshing = state.loading,
                onRefresh = { action(SwipeToRefresh) },
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    contentPadding = contentPadding,
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (person in state.people) {
                        item {
                            PersonThumbnail(
                                person = person,
                                shape = MaterialTheme.shapes.medium,
                                onPersonSelected = { action(PersonSelected(person)) }
                            )
                        }
                    }
                }
            }
        }
    }
}