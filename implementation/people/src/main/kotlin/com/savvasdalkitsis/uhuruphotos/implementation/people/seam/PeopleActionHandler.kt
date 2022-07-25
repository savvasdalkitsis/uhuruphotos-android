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
package com.savvasdalkitsis.uhuruphotos.implementation.people.seam

import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction.LoadPeople
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleAction.ToggleSortOrder
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffect.ErrorLoadingPeople
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleMutation.DisplayPeople
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleMutation.Loading
import com.savvasdalkitsis.uhuruphotos.implementation.people.seam.PeopleMutation.SetSortOrder
import com.savvasdalkitsis.uhuruphotos.implementation.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.implementation.people.view.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.implementation.people.view.state.SortOrder.ASCENDING
import com.savvasdalkitsis.uhuruphotos.implementation.people.view.state.SortOrder.DESCENDING
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
    private val photosUseCase: PhotosUseCase,
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
                with(photosUseCase) {
                    DisplayPeople(
                        when (sortOrder) {
                            ASCENDING -> people
                            DESCENDING -> people.reversed()
                        }.map {
                            it.toPerson { url -> url.toAbsoluteUrl() }
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
        try {
            peopleUseCase.refreshPeople()
        } catch (e: Exception) {
            log(e)
            effect(ErrorLoadingPeople)
        } finally {
            loading.emit(false)
        }
    }

}
