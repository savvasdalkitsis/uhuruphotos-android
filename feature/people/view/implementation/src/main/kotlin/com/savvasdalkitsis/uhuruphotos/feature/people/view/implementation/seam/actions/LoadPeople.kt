package com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleEffect
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.seam.PeopleMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.implementation.ui.state.SortOrder
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

object LoadPeople : PeopleAction() {

    context(PeopleActionsContext) override fun handle(
        state: PeopleState,
        effect: EffectHandler<PeopleEffect>
    ) = merge(
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
                        SortOrder.ASCENDING -> people
                        SortOrder.DESCENDING -> people.reversed()
                    }.map {
                        it.toPerson { url -> url.toRemoteUrl() }
                    }
                )
            }
        },
    )

}