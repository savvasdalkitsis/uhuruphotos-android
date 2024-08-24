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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.QueryChanged
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.RemoveFromRecentSearches
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.SearchFor
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.UpsellLoginFromSearch
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.PersonSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PersonImage
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchField
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchSuggestion
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun SearchField(
    state: DiscoverState,
    action: (DiscoverAction) -> Unit,
) {
    val leading = remember {
        persistentMapOf<String, @Composable (SearchSuggestion) -> Unit>(
            "recent" to {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    painter = painterResource(id = drawable.ic_history),
                    contentDescription = null
                )
            },
            "server" to {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    painter = painterResource(id = drawable.ic_assistant),
                    contentDescription = null
                )
            },
            "person" to {
                (it as? PersonSearchSuggestion)?.person?.let { person ->
                    PersonImage(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        person = person
                    )
                }
            }
        )
    }
    val trailing = remember {
        persistentMapOf<String, @Composable (SearchSuggestion) -> Unit>(
            "recent" to {
                ActionIcon(
                    onClick = { action(RemoveFromRecentSearches(it.filterable)) },
                    icon = drawable.ic_clear
                )
            }
        )
    }
    SearchField(
        queryCacheKey = state.queryCacheKey,
        latestQuery = state.latestQuery,
        enabled = state.isSearchEnabled,
        searchSuggestions = state.searchSuggestions,
        suggestionLeadingContent = leading,
        suggestionTrailingContent = trailing,
        onDisabledClick = { action(UpsellLoginFromSearch) },
        onNewQuery = { action(QueryChanged(it)) },
        onSearchFor = { action(SearchFor(it)) },
        onSearchForSuggestion = {
            when (it) {
                is PersonSearchSuggestion -> action(PersonSelected(it.person))
                else -> action(SearchFor(it.filterable))
            }
        }
    )
}