package com.savvasdalkitsis.uhuruphotos.search.view

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchAction
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState

@Composable
fun SearchIdle(
    state: SearchState,
    action: (SearchAction) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
        SearchLocations(action)
        Spacer(modifier = Modifier.height(16.dp))
    }
}