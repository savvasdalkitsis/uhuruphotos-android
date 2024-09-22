/*
Copyright 2024 Savvas Dalkitsis

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
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.RemoveFromRecentSearches
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.RecentSearchSuggestionState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search.SearchSuggestion
import kotlinx.collections.immutable.persistentMapOf

fun suggestionTrailingContent(action: (DiscoverAction) -> Unit) = persistentMapOf<String, @Composable (SearchSuggestion) -> Unit>(
    RecentSearchSuggestionState.TYPE to {
        ActionIcon(
            onClick = { action(RemoveFromRecentSearches(it.filterable)) },
            icon = R.drawable.ic_clear
        )
    }
)