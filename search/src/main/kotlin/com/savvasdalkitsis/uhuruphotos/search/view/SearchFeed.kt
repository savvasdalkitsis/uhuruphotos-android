package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feed.view.Feed
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.copy
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState

@Composable
fun SearchFeed(
    state: SearchState,
    contentPadding: PaddingValues,
    searchResults: SearchResults.Found,
    action: (SearchAction) -> Unit
) {
    Feed(
        contentPadding = contentPadding.copy(top = 0.dp),
        onPhotoSelected = { photo, center, scale ->
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