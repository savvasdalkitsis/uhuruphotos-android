package com.savvasdalkitsis.librephotos.search.viewmodel

import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.view.state.SearchResults
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer

fun searchReducer() : com.savvasdalkitsis.librephotos.viewmodel.Reducer<SearchState, SearchMutation> = { state, mutation ->
    when (mutation) {
        is SearchMutation.QueryChanged -> state.copy(query = mutation.query)
        is SearchMutation.FocusChanged -> state.copy(showClearButton = mutation.focused)
        SearchMutation.SearchCleared -> state.copy(query = "")
        SearchMutation.SearchStarted -> state.copy(searchResults = SearchResults.Searching)
        is SearchMutation.SearchResultsUpdated -> state.copy(
            searchResults = when {
                mutation.albums.isEmpty() -> SearchResults.Searching
                else -> SearchResults.Found(mutation.albums)
            }
        )
        is SearchMutation.UserBadgeStateChanged -> state.copy(userBadgeState = mutation.userBadgeState)
        SearchMutation.HideAccountOverview -> state.copy(showAccountOverview = false)
        SearchMutation.ShowAccountOverview -> state.copy(showAccountOverview = true)
        is SearchMutation.ChangeDisplay -> state.copy(feedDisplay = mutation.feedDisplay)
    }
}
