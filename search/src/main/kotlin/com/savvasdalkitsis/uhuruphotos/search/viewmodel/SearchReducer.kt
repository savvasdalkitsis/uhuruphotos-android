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
package com.savvasdalkitsis.uhuruphotos.search.viewmodel

import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation
import com.savvasdalkitsis.uhuruphotos.search.mvflow.SearchMutation.*
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchResults
import com.savvasdalkitsis.uhuruphotos.search.view.state.SearchState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun searchReducer(): Reducer<SearchState, SearchMutation> = { state, mutation ->
    when (mutation) {
        is FocusChanged -> state.copy(showClearButton = mutation.focused)
        SwitchStateToSearching -> state.copy(searchResults = SearchResults.Searching)
        SwitchStateToIdle -> state.copy(searchResults = SearchResults.Idle)
        is SwitchStateToFound -> state.copy(searchResults = mutation.found)
        is UserBadgeStateChanged -> state.copy(userInformationState = mutation.userInformationState)
        HideAccountOverview -> state.copy(showAccountOverview = false)
        ShowAccountOverview -> state.copy(showAccountOverview = true)
        is ChangeFeedDisplay -> state.copy(feedDisplay = mutation.display)
        is ChangeSearchDisplay -> state.copy(searchDisplay = mutation.display)
        HideLogOutConfirmation -> state.copy(showLogOutConfirmation = false)
        ShowLogOutConfirmation -> state.copy(showLogOutConfirmation = true)
        is ShowSearchSuggestion -> state.copy(suggestion = mutation.suggestion)
        HideSuggestions -> state.copy(suggestion = null)
        is ShowPeople -> state.copy(people = mutation.people)
        is ShowSearchSuggestions -> state.copy(searchSuggestions = mutation.suggestions)
        is UpdateLatestQuery -> state.copy(latestQuery = mutation.query)
    }
}
