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
package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrors
import com.savvasdalkitsis.uhuruphotos.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder.ASCENDING
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder.DESCENDING
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.LoadPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.ToggleSortOrder
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect.ErrorLoadingPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleMutation.DisplayPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleMutation.SetSortOrder
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PeopleHandler @Inject constructor(
    private val peopleUseCase: PeopleUseCase,
) : Handler<PeopleState, PeopleEffect, PeopleAction, PeopleMutation> {

    private
    val sort: MutableSharedFlow<SortOrder> = MutableStateFlow(SortOrder.default)

    override fun invoke(
        state: PeopleState,
        action: PeopleAction,
        effect: suspend (PeopleEffect) -> Unit
    ): Flow<PeopleMutation> = when (action) {
        LoadPeople -> combine(
            peopleUseCase.observePeopleByName()
                .onErrors {
                    effect(ErrorLoadingPeople)
                },
            sort
        ) { people, sortOrder ->
            DisplayPeople(when (sortOrder) {
                ASCENDING -> people
                DESCENDING -> people.reversed()
            })
        }
        ToggleSortOrder -> flow {
            val sortOrder = state.sortOrder.toggle()
            emit(SetSortOrder(sortOrder))
            sort.emit(sortOrder)
        }
        NavigateBack -> flow {
            effect(PeopleEffect.NavigateBack)
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person))
        }
    }

}
