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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrderState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toImmutableList

sealed class PeopleMutation(
    mutation: Mutation<PeopleState>,
) : Mutation<PeopleState> by mutation {

    data class Loading(val loading: Boolean) : PeopleMutation({
        it.copy(loading = loading)
    })

    data class DisplayPeople(val people: List<PersonState>) : PeopleMutation({
        it.copy(people = people.toImmutableList())
    })

    data class SetSortOrder(val sortOrderState: SortOrderState) : PeopleMutation({
        it.copy(sortOrderState = sortOrderState)
    })
}
