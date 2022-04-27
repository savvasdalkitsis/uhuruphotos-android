package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun searchReducer() : Reducer<SearchState, SearchMutation> = { state, mutation ->
    when (mutation) {
        is QueryChanged -> state.copy(query = mutation.query)
        is FocusChanged -> state.copy(showClearButton = mutation.focused)
        SearchCleared -> state.copy(query = "")
        SearchStarted -> state.copy(searchResults = SearchResults.Searching)
        is SearchResultsUpdated -> state.copy(
            searchResults = when {
                mutation.albums.isEmpty() -> SearchResults.Searching
                else -> SearchResults.Found(mutation.albums)
            }
        )
        is UserBadgeStateChanged -> state.copy(userInformationState = mutation.userInformationState)
        HideAccountOverview -> state.copy(showAccountOverview = false)
        ShowAccountOverview -> state.copy(showAccountOverview = true)
        is ChangeFeedDisplay -> state.copy(feedDisplay = mutation.display)
        is ChangeSearchDisplay -> state.copy(searchDisplay = mutation.display)
        HideLogOutConfirmation -> state.copy(showLogOutConfirmation = false)
        ShowLogOutConfirmation -> state.copy(showLogOutConfirmation = true)
    }
}
