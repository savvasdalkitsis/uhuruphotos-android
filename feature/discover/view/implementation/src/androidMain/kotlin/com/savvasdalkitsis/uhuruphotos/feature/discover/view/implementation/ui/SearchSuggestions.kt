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

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam.actions.DiscoverAction
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.SearchSuggestion

@Composable
internal fun SearchSuggestions(
    state: DiscoverState,
    action: (DiscoverAction) -> Unit,
    onSearchAction: (String) -> Unit,
) {
    Popup {
        Card(
            modifier = Modifier.clip(MaterialTheme.shapes.large)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            elevation = 2.dp,
        ) {
            val height = when (LocalConfiguration.current.orientation) {
                ORIENTATION_LANDSCAPE -> 120.dp
                else -> 320.dp
            }
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = height)
                    .animateContentSize()
            ) {
                for (suggestion in state.searchSuggestions) {
                    when (suggestion) {
                        is SearchSuggestion.RecentSearchSuggestion -> recentSearchSuggestion(
                            suggestion = suggestion.query,
                            action = action
                        ) {
                            onSearchAction(suggestion.query)
                        }
                        is SearchSuggestion.ServerSearchSuggestion -> serverSearchSuggestion(
                            suggestion = suggestion.query
                        ) {
                            onSearchAction(suggestion.query)
                        }
                        is SearchSuggestion.PersonSearchSuggestion -> personSuggestion(
                            suggestion.person,
                            action,
                        )
                    }
                }
            }
        }
    }
}