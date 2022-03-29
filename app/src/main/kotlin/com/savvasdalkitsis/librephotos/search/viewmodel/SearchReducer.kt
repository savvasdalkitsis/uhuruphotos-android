package com.savvasdalkitsis.librephotos.search.viewmodel

import com.savvasdalkitsis.librephotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.librephotos.search.view.state.SearchResults
import com.savvasdalkitsis.librephotos.search.view.state.SearchState
import net.pedroloureiro.mvflow.Reducer

class SearchReducer : Reducer<SearchState, SearchMutation> {

    override fun invoke(
        state: SearchState,
        mutation: SearchMutation,
    ): SearchState = when (mutation) {
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
    }

}
