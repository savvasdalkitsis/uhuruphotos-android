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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonCollageState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toImmutableList

sealed class PersonMutation(
    mutation: Mutation<PersonCollageState>,
) : Mutation<PersonCollageState> by mutation {

    data object Loading : PersonMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    data class ShowPersonMedia(val clusterStates: List<ClusterState>) : PersonMutation({
        it.copyFeed { copy(
            isLoading = false,
            clusters = clusterStates.toImmutableList(),
        ) }
    })

    data class ShowPersonDetails(val personState: PersonState) : PersonMutation({
        it.copy(personState = personState)
    })

    data class SetFeedDisplay(val display: CollageDisplayState) : PersonMutation({
        it.copyFeed { copy(collageDisplayState = display) }
    })
}

private fun PersonCollageState.copyFeed(feedCopy: CollageState.() -> CollageState): PersonCollageState =
    copy(collageState = collageState.feedCopy())