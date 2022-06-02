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
package com.savvasdalkitsis.uhuruphotos.person.seam

import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.person.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.LoadPerson
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonMutation.Loading
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonMutation.SetFeedDisplay
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonMutation.ShowPersonDetails
import com.savvasdalkitsis.uhuruphotos.person.seam.PersonMutation.ShowPersonPhotos
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class PersonHandler @Inject constructor(
    private val personUseCase: PersonUseCase,
    private val peopleUseCase: PeopleUseCase,
    private val photosUseCase: PhotosUseCase,
) : ActionHandler<PersonState, PersonEffect, PersonAction, PersonMutation> {

    override fun handleAction(
        state: PersonState,
        action: PersonAction,
        effect: suspend (PersonEffect) -> Unit
    ): Flow<PersonMutation> = when (action) {
        is LoadPerson -> merge(
            flowOf(Loading),
            peopleUseCase.observePerson(action.id)
                .map { with(photosUseCase) {
                    it.toPerson { it.toAbsoluteUrl() }
                } }
                .map(::ShowPersonDetails),
            personUseCase.observePersonAlbums(action.id)
                .map(::ShowPersonPhotos)
        )
        NavigateBack -> flow {
            effect(PersonEffect.NavigateBack)
        }
        is ChangeDisplay -> flowOf(SetFeedDisplay(action.display))
        is SelectedPhoto -> flow {
            effect(with(action) {
                OpenPhotoDetails(photo.id, center, scale, photo.isVideo, state.person!!)
            })
        }
    }

}
