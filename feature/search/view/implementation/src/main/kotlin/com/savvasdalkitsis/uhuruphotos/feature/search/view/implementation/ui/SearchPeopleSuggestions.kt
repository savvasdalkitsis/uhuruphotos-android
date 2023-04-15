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
package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.PeopleBanner
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.SearchAction
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions.ViewAllPeopleSelected

@Composable
fun SearchPeopleSuggestions(
    people: List<Person>,
    action: (SearchAction) -> Unit,
) {
    PeopleBanner(
        people = people,
        onViewAllClicked = { action(ViewAllPeopleSelected) },
        onPersonSelected = { action(PersonSelected(it)) }
    )
}