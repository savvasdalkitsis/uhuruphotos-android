package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrors
import com.savvasdalkitsis.uhuruphotos.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder.ASCENDING
import com.savvasdalkitsis.uhuruphotos.people.view.state.SortOrder.DESCENDING
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleAction.*
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect.ErrorLoadingPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleMutation.DisplayPeople
import com.savvasdalkitsis.uhuruphotos.people.viewmodel.PeopleMutation.SetSortOrder
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.*
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
            peopleUseCase.getPeopleByName()
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
