/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.effects.PersonEffect
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonMutation
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

data class LoadPerson(val id: Int) : PersonAction() {
    context(PersonActionsContext) override fun handle(
        state: PersonState,
        effect: EffectHandler<PersonEffect>
    ) = merge(
        flowOf(PersonMutation.Loading),
        peopleUseCase.observePerson(id)
            .map {
                val serverUrl = serverUseCase.getServerUrl()!!
                it.toPerson { url ->
                    "$serverUrl$url"
                }
            }
            .map(PersonMutation::ShowPersonDetails),
        personUseCase.observePersonMedia(id)
            .map { it.map(MediaCollection::toCluster) }
            .map(PersonMutation::ShowPersonMedia)
    )
}
