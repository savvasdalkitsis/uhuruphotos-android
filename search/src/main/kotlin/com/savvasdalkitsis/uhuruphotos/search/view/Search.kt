package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults.*
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.ui.view.FullProgressBar

@Composable fun Search(
    state: SearchState,
    action: (SearchAction) -> Unit,
    contentPadding: PaddingValues,
) {
    Column {
        Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
        SearchField(state, action)
        when (state.searchResults) {
            Idle -> SearchIdle(state, action)
            Searching -> FullProgressBar()
            is Found -> SearchFeed(state, contentPadding, state.searchResults, action)
        }
    }
}