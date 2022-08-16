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
package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonAction.LoadPerson
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation.SetFeedDisplay
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation.ShowPersonDetails
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation.ShowPersonPhotos
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class PersonActionHandler @Inject constructor(
    private val personUseCase: PersonUseCase,
    private val peopleUseCase: PeopleUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
) : ActionHandler<PersonState, PersonEffect, PersonAction, PersonMutation> {

    override fun handleAction(
        state: PersonState,
        action: PersonAction,
        effect: suspend (PersonEffect) -> Unit
    ): Flow<PersonMutation> = when (action) {
        is LoadPerson -> merge(
            flowOf(Loading),
            peopleUseCase.observePerson(action.id)
                .map { with(remoteMediaUseCase) {
                    it.toPerson { it.toRemoteUrl() }
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
                OpenPhotoDetails(mediaItem.id, center, scale, mediaItem.isVideo, state.person!!)
            })
        }
    }

}
