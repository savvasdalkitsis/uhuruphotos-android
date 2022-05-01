package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class SearchReducer @Inject constructor(
    private val photosUseCase: PhotosUseCase,
) : Reducer<SearchState, SearchMutation> {

    override suspend fun invoke(state: SearchState, mutation: SearchMutation): SearchState =
        when (mutation) {
            is QueryChanged -> state.copy(query = mutation.query)
            is FocusChanged -> state.copy(showClearButton = mutation.focused)
            SearchCleared -> state.copy(query = "")
            SearchStarted -> state.copy(searchResults = SearchResults.Searching)
            SearchStopped -> state.copy(searchResults = SearchResults.Idle)
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
            is ShowSearchSuggestion -> state.copy(suggestion = mutation.suggestion)
            HideSuggestions -> state.copy(suggestion = null)
            is ShowPeople -> with(photosUseCase) {
                state.copy(people = mutation.people
                    .subList(0, max(0, min(10, mutation.people.size - 1)))
                    .map { it.toPerson { url -> url.toAbsoluteUrl() } })
            }
        }
}
