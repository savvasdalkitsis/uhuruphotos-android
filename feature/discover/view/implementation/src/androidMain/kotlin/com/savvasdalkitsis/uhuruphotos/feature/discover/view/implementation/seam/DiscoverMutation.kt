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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.DiscoverState
import com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state.SearchSuggestion
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toPersistentList

sealed class DiscoverMutation(
    mutation: Mutation<DiscoverState>,
) : Mutation<DiscoverState> by mutation {

    data object HideSuggestions : DiscoverMutation({
        it.copy(suggestion = null)
    })

    data object HideLoginUpsellDialogs : DiscoverMutation({
        it.copy(
            showLoginUpsellDialogFromPeople = false,
            showLoginUpsellDialogFromSearch = false,
        )
    })

    data object ShowLoginUpsellDialogFromPeople : DiscoverMutation({
        it.copy(showLoginUpsellDialogFromPeople = true)
    })

    data object ShowLoginUpsellDialogFromSearch : DiscoverMutation({
        it.copy(showLoginUpsellDialogFromSearch = true)
    })

    data object EnableSearch : DiscoverMutation({
        it.copy(isSearchEnabled = true)
    })

    data object DisableSearch : DiscoverMutation({
        it.copy(isSearchEnabled = false)
    })

    data class ChangeFeedDisplay(val display: CollageDisplay) : DiscoverMutation({
        it.copy(collageDisplay = display)
    })

    data class FocusChanged(val focused: Boolean) : DiscoverMutation({
        it.copy(showClearButton = focused)
    })

    data class ShowSearchSuggestion(val suggestion: String) : DiscoverMutation({
        it.copy(suggestion = suggestion)
    })

    data class ShowPeople(val people: List<Person>) : DiscoverMutation({
        it.copy(people = people.toPersistentList())
    })

    data class ShowPeopleUpsell(val show: Boolean) : DiscoverMutation({
        it.copy(showPeopleUpsell = show)
    })

    data class ShowSearchSuggestions(
        val suggestions: List<SearchSuggestion>,
    ) : DiscoverMutation({
        it.copy(searchSuggestions = suggestions.toPersistentList())
    })

    data class UpdateLatestQuery(val query: String) : DiscoverMutation({
        it.copy(latestQuery = query)
    })

    data class BustQueryCache(val hash: String) : DiscoverMutation({
        it.copy(queryCacheKey = hash)
    })

    data class ShowLibrary(val showLibrary: Boolean) : DiscoverMutation({
        it.copy(showLibrary = showLibrary)
    })

    data class ChangeMapViewport(val viewport: Viewport) : DiscoverMutation({
        it.copy(mapViewport = viewport)
    })
}