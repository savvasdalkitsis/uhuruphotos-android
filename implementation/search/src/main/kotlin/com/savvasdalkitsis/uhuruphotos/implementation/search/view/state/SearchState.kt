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
package com.savvasdalkitsis.uhuruphotos.implementation.search.view.state

import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.api.userbadge.view.state.UserInformationState

data class SearchState(
    val showClearButton: Boolean = false,
    val searchResults: SearchResults = SearchResults.Idle,
    val userInformationState: UserInformationState = UserInformationState(),
    val showAccountOverview: Boolean = false,
    val showLogOutConfirmation: Boolean = false,
    val feedDisplay: FeedDisplay = FeedDisplays.default,
    val searchDisplay: FeedDisplay = FeedDisplays.default,
    val showLibrary: Boolean = true,
    val suggestion: String? = null,
    val people: List<Person> = emptyList(),
    val searchSuggestions: List<SearchSuggestion> = emptyList(),
    val latestQuery: String = "",
)