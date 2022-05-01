package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.person.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonAction.*
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonMutation.*
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PersonHandler @Inject constructor(
    private val personUseCase: PersonUseCase,
    private val peopleUseCase: PeopleUseCase,

) : Handler<PersonState, PersonEffect, PersonAction, PersonMutation> {

    override fun invoke(
        state: PersonState,
        action: PersonAction,
        effect: suspend (PersonEffect) -> Unit
    ): Flow<PersonMutation> = when (action) {
        is LoadPerson -> merge(
            flowOf(Loading),
            peopleUseCase.getPerson(action.id)
                .map(::ShowPersonDetails),
            personUseCase.getPersonAlbums(action.id)
                .map(::ShowPersonPhotos)
        )
        NavigateBack -> flow {
            effect(PersonEffect.NavigateBack)
        }
        is ChangeDisplay -> flowOf(SetFeedDisplay(action.display))
        is SelectedPhoto -> flow {
            effect(
                OpenPhotoDetails(action.photo.id, action.center, action.scale, action.photo.isVideo)
            )
        }
    }

}
