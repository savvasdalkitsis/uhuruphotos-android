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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.AutoAlbumSelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.QueryChanged
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.SearchFor
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.UpsellLoginFromSearch
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.UserAlbumSelected
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.AutoAlbumSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.PersonSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.UserAlbumSearchSuggestion
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchField

@Composable
fun DiscoverSearchField(
    state: DiscoverState,
    action: (DiscoverAction) -> Unit,
) {
    val leading = remember { suggestionLeadingContent }
    val trailing = remember(action) { suggestionTrailingContent(action) }
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
                is UserAlbumSearchSuggestion -> action(UserAlbumSelected(it.userAlbums))
                is AutoAlbumSearchSuggestion -> action(AutoAlbumSelected(it.autoAlbum))
                else -> action(SearchFor(it.filterable))
            }
        },
    )
}