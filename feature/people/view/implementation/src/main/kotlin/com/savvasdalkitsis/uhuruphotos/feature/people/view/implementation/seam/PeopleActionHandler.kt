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

import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleAction.LoadPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleAction.ToggleSortOrder
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleEffect.ErrorLoadingPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleMutation.DisplayPeople
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleMutation.SetSortOrder
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder.ASCENDING
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder.DESCENDING
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class PeopleActionHandler @Inject constructor(
    private val peopleUseCase: PeopleUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
) : ActionHandler<PeopleState, PeopleEffect, PeopleAction, PeopleMutation> {

    private
    val sort: MutableSharedFlow<SortOrder> = MutableStateFlow(SortOrder.default)
    private
    val loading: MutableSharedFlow<Boolean> = MutableStateFlow(false)

    override fun handleAction(
        state: PeopleState,
        action: PeopleAction,
        effect: suspend (PeopleEffect) -> Unit
    ): Flow<PeopleMutation> = when (action) {
        LoadPeople -> merge(
            loading.map { Loading(it) },
            combine(
                sort,
                peopleUseCase.observePeopleByName()
                    .safelyOnStartIgnoring {
                        if (peopleUseCase.getPeopleByName().isEmpty()) {
                            refresh(effect)
                        }
                    },
            ) { sortOrder, people ->
                with(remoteMediaUseCase) {
                    DisplayPeople(
                        when (sortOrder) {
                            ASCENDING -> people
                            DESCENDING -> people.reversed()
                        }.map {
                            it.toPerson { url -> url.toRemoteUrl() }
                        }
                    )
                }
            },
        )
        SwipeToRefresh -> flow {
            refresh(effect)
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

    private suspend fun refresh(effect: suspend (PeopleEffect) -> Unit) {
        loading.emit(true)
        val result = peopleUseCase.refreshPeople()
        if (result.isFailure) {
            effect(ErrorLoadingPeople)
        }
        loading.emit(false)
    }

}
