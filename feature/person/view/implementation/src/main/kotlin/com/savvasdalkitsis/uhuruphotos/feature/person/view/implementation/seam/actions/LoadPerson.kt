package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect
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
            .map { with(remoteMediaUseCase) {
                it.toPerson { it.toRemoteUrl() }
            } }
            .map(PersonMutation::ShowPersonDetails),
        personUseCase.observePersonMedia(id)
            .map { it.map(MediaCollection::toCluster) }
            .map(PersonMutation::ShowPersonMedia)
    )
}