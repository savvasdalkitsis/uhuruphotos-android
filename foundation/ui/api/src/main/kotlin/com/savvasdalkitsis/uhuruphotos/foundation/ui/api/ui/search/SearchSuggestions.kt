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

package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Composable
internal fun SharedTransitionScope.SearchSuggestions(
    suggestions: ImmutableList<SearchSuggestion>,
    leadingContent: ImmutableMap<String, @Composable SharedTransitionScope.(SearchSuggestion) -> Unit> = persistentMapOf(),
    trailingContent: ImmutableMap<String, @Composable (SearchSuggestion) -> Unit> = persistentMapOf(),
    onSearchAction: (SearchSuggestion) -> Unit,
) {
    Popup {
        Card(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = CardDefaults.elevatedShape,
            colors = CardDefaults.elevatedCardColors(),
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
                for ((index, suggestion) in suggestions.withIndex()) {
                    item(index, contentType = "${suggestion.type}SearchSuggestion") {
                        Suggestion(
                            modifier = Modifier.animateItem(),
                            text = suggestion.filterable,
                            onClick = {
                                onSearchAction(suggestion)
                            },
                            leadingContent = {
                                leadingContent[suggestion.type]?.invoke(this, suggestion)
                            },
                            trailingContent = {
                                trailingContent[suggestion.type]?.invoke(suggestion)
                            }
                        )
                    }
                }
            }
        }
    }
}