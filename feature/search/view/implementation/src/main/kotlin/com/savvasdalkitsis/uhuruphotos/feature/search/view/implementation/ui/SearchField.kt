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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.ChangeFocus
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.QueryChanged
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchCleared
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchFor
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchResults.Idle
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
fun SearchField(
    state: SearchState,
    action: (SearchAction) -> Unit,
) {
    var query by remember(state.queryCacheKey) { mutableStateOf(state.latestQuery) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(100))
    ) {
        fun changeQuery(newQuery: String) {
            query = newQuery
            action(QueryChanged(newQuery))
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { action(ChangeFocus(it.isFocused)) },
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                Row {
                    AnimatedVisibility(
                        visible = state.showClearButton,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            changeQuery("")
                            action(SearchCleared)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(string.clear)
                            )
                        }
                    }

                    IconButton(onClick = { action(SearchFor(query)) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(string.search_icon)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = { action(SearchFor(query)) }
            ),
            placeholder = { Text(stringResource(string.search_for_something)) },
            value = query,
            onValueChange = ::changeQuery
        )
        AnimatedVisibility(
            visible = state.searchResults is Idle
                    && query.isNotEmpty()
                    && state.searchSuggestions.isNotEmpty(),
        ) {
            SearchSuggestions(state, action) {
                query = it
                action(SearchFor(it))
            }
        }
    }
}