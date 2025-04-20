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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrderState.ASCENDING
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrderState.DESCENDING
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.refresh.SwipeRefresh
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruUpNavButton
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_sort_az_ascending
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_sort_az_descending

@Composable
fun People(
    state: PeopleState,
    action: (PeopleAction) -> Unit,
) {
    UhuruScaffold(
        title = { Text("People") },
        navigationIcon = { UhuruUpNavButton() },
        actionBarContent = {
            UhuruActionIcon(onClick = { action(ToggleSortOrder) }, icon = when (state.sortOrderState) {
                ASCENDING -> drawable.ic_sort_az_ascending
                DESCENDING -> drawable.ic_sort_az_descending
            })
        }
    ) { contentPadding ->
        if (state.people.isEmpty()) {
            UhuruFullLoading()
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
                                personState = person,
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