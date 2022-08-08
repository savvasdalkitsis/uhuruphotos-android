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
package com.savvasdalkitsis.uhuruphotos.implementation.search.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.compose.copy
import com.savvasdalkitsis.uhuruphotos.api.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.implementation.search.seam.SearchAction
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.implementation.search.view.state.SearchState

@Composable
fun SearchFeed(
    state: SearchState,
    contentPadding: PaddingValues,
    searchResults: SearchResults.Found,
    action: (SearchAction) -> Unit
) {
    Feed(
        contentPadding = contentPadding.copy(top = 0.dp),
        onMediaItemSelected = { photo, center, scale ->
            action(SearchAction.SelectedPhoto(photo, center, scale))
        },
        onChangeDisplay = { action(SearchAction.ChangeDisplay(it)) },
        state = FeedState(
            isLoading = false,
            albums = searchResults.albums,
            feedDisplay = state.searchDisplay,
        ),
    )
}