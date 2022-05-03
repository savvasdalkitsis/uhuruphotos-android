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
package com.savvasdalkitsis.uhuruphotos.search.view.state

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

data class SearchState(
    val query: String = "",
    val showClearButton: Boolean = false,
    val searchResults: SearchResults = SearchResults.Idle,
    val userInformationState: UserInformationState = UserInformationState(),
    val showAccountOverview: Boolean = false,
    val showLogOutConfirmation: Boolean = false,
    val feedDisplay: FeedDisplay = FeedDisplays.default,
    val searchDisplay: FeedDisplay = FeedDisplays.default,
    val suggestion: String? = null,
    val people: List<Person> = emptyList()
)