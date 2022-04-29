package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.copy
import com.savvasdalkitsis.uhuruphotos.navigation.ControllersProvider
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction.*
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar

@Composable fun Search(
    state: SearchState,
    action: (SearchAction) -> Unit,
    controllersProvider: ControllersProvider,
    contentPadding: PaddingValues,
) {
    Column {
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .onFocusChanged { focusState ->
                    action(ChangeFocus(focusState.isFocused))
                }
                .focusRequester(controllersProvider.focusRequester!!),
            maxLines = 1,
            singleLine = true,
            trailingIcon = {
                AnimatedVisibility(
                    visible = state.showClearButton,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = { action(ClearSearch) }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = { action(SearchFor(state.query)) }
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "searchIcon"
                )
            },
            label = { Text("Search for something") },
            value = state.query,
            onValueChange = {
                action(ChangeQuery(it))
            }
        )
        val suggestion = state.suggestion
        AnimatedVisibility(visible = suggestion != null) {
            if (suggestion != null) {
                Row(
                    modifier = Modifier
                        .padding(
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(modifier = Modifier.weight(1f), text = "Search suggestions:")
                    OutlinedButton(onClick = {
                        action(ChangeQuery(suggestion))
                        action(SearchFor(suggestion))
                    }) {
                        Text(suggestion)
                    }
                }
            }
        }
        when (state.searchResults) {
            SearchResults.Idle -> {}
            SearchResults.Searching -> FullProgressBar()
            is SearchResults.Found -> Feed(
                contentPadding = contentPadding.copy(top = 0.dp),
                onPhotoSelected = { photo, center, scale ->
                    action(SelectedPhoto(photo, center, scale))
                },
                onChangeDisplay = { action(ChangeDisplay(it)) },
                state = FeedState(
                    isLoading = false,
                    albums = state.searchResults.albums,
                    feedDisplay = state.searchDisplay,
                ),
            )
        }
    }
}