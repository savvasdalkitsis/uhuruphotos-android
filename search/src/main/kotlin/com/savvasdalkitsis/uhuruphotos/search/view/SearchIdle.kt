package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState

@Composable
fun SearchIdle(
    state: SearchState,
    action: (SearchAction) -> Unit
) {
    val suggestion = state.suggestion
    AnimatedVisibility(
        visible = suggestion != null,
        enter = slideInVertically() + expandVertically(),
        exit = slideOutHorizontally() + shrinkVertically(),
    ) {
        if (suggestion != null) {
            SearchSuggestion(suggestion, action)
        }
    }
    AnimatedVisibility(
        visible = state.people.isNotEmpty(),
        enter = slideInVertically() + expandVertically(),
        exit = slideOutHorizontally() + shrinkVertically(),
    ) {
        SearchPeopleSuggestions(state.people, action)
    }
}