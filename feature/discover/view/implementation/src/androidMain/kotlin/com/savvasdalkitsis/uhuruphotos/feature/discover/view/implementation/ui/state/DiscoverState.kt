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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.Locations.TRAFALGAR_SQUARE
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class DiscoverState(
    val showClearButton: Boolean = false,
    val collageDisplay: CollageDisplay = PredefinedCollageDisplay.default,
    val showLibrary: Boolean = true,
    val showLoginUpsellDialogFromSearch: Boolean = false,
    val showLoginUpsellDialogFromPeople: Boolean = false,
    val isSearchEnabled: Boolean = false,
    val suggestion: String? = null,
    val people: ImmutableList<Person> = persistentListOf(),
    val showPeopleUpsell: Boolean = false,
    val searchSuggestions: ImmutableList<SearchSuggestion> = persistentListOf(),
    val latestQuery: String = "",
    val queryCacheKey: String = "",
    val mapViewport: Viewport = TRAFALGAR_SQUARE,
)