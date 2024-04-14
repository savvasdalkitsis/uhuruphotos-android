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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toPersistentList

sealed class PersonMutation(
    mutation: Mutation<PersonState>,
) : Mutation<PersonState> by mutation {

    data object Loading : PersonMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    data class ShowPersonMedia(val clusters: List<Cluster>) : PersonMutation({
        it.copyFeed { copy(
            isLoading = false,
            clusters = clusters.toPersistentList(),
        ) }
    })

    data class ShowPersonDetails(val person: Person) : PersonMutation({
        it.copy(person = person)
    })

    data class SetFeedDisplay(val display: CollageDisplay) : PersonMutation({
        it.copyFeed { copy(collageDisplay = display) }
    })
}

private fun PersonState.copyFeed(feedCopy: CollageState.() -> CollageState): PersonState =
    copy(collageState = collageState.feedCopy())