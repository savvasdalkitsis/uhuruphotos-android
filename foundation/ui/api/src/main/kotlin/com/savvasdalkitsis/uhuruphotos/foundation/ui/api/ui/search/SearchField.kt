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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.LocalWindowSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.clear
import uhuruphotos_android.foundation.strings.api.generated.resources.search_for_something
import uhuruphotos_android.foundation.strings.api.generated.resources.search_icon

@Composable
fun SearchField(
    queryCacheKey: String,
    latestQuery: String,
    enabled: Boolean = true,
    showSearchIcon: Boolean = true,
    searchSuggestions: ImmutableList<SearchSuggestion> = persistentListOf(),
    suggestionLeadingContent: ImmutableMap<String, @Composable (SearchSuggestion) -> Unit> = persistentMapOf(),
    suggestionTrailingContent: ImmutableMap<String, @Composable (SearchSuggestion) -> Unit> = persistentMapOf(),
    autoFocus: Boolean = false,
    onDisabledClick: () -> Unit = {},
    onNewQuery: (String) -> Unit,
    onSearchForSuggestion: (SearchSuggestion) -> Unit = {},
    onSearchFor: (String) -> Unit,
) {
    var query by remember(queryCacheKey) { mutableStateOf(latestQuery) }
    var showClearButton by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(MaterialTheme.shapes.large)
    ) {
        fun changeQuery(newQuery: String) {
            query = newQuery
            onNewQuery(newQuery)
        }
        val focusRequester = remember { FocusRequester() }

        val modifier = remember(enabled) {
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { showClearButton = it.isFocused }
                .run {
                    if (enabled)
                        this
                    else
                        clickable { onDisabledClick() }
                }
        }

        TextField(
            modifier = modifier,
            enabled = enabled,
            maxLines = 1,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            trailingIcon = {
                Row {
                    AnimatedVisibility(
                        visible = showClearButton,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = {
                            changeQuery("")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(string.clear)
                            )
                        }
                    }

                    if (showSearchIcon) {
                        IconButton(
                            enabled = enabled,
                            onClick = { onSearchFor(query) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(string.search_icon)
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchFor(query) }
            ),
            placeholder = { Text(stringResource(string.search_for_something)) },
            value = query,
            onValueChange = ::changeQuery
        )

        LaunchedEffect(Unit) {
            if (autoFocus) {
                focusRequester.requestFocus()
            }
        }

        val heightShort = LocalWindowSize.current.heightSizeClass == WindowHeightSizeClass.Compact
        AnimatedVisibility(
            visible = !heightShort && query.isNotEmpty() && searchSuggestions.isNotEmpty(),
        ) {
            SearchSuggestions(
                suggestions = searchSuggestions,
                leadingContent = suggestionLeadingContent,
                trailingContent = suggestionTrailingContent,
            ) {
                query = it.filterable
                onSearchForSuggestion(it)
            }
        }
    }
}