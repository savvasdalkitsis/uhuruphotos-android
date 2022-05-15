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
            peopleUseCase.observePerson(action.id)
                .map(::ShowPersonDetails),
            personUseCase.getPersonAlbums(action.id)
                .map(::ShowPersonPhotos)
        )
        NavigateBack -> flow {
            effect(PersonEffect.NavigateBack)
        }
        is ChangeDisplay -> flowOf(SetFeedDisplay(action.display))
        is SelectedPhoto -> flow {
            effect(with(action) {
                OpenPhotoDetails(photo.id, center, scale, photo.isVideo)
            })
        }
    }

}
