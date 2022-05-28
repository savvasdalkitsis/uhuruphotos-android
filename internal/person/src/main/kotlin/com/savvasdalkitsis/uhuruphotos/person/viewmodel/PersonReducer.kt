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

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonMutation.Loading
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonMutation.SetFeedDisplay
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonMutation.ShowPersonDetails
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonMutation.ShowPersonPhotos
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer
import javax.inject.Inject

class PersonReducer @Inject constructor(
    private val photosUseCase: PhotosUseCase,
) : Reducer<PersonState, PersonMutation> {
    override suspend fun invoke(
        state: PersonState,
        mutation: PersonMutation,
    ): PersonState =
        when (mutation) {
            is Loading -> state.copyFeed { copy(isLoading = true) }
            is ShowPersonDetails -> with(photosUseCase) {
                state.copy(person = mutation.person.toPerson { it.toAbsoluteUrl() })
            }
            is ShowPersonPhotos -> state.copyFeed { copy(
                isLoading = false,
                albums = mutation.albums,
            ) }
            is SetFeedDisplay -> state.copyFeed { copy(feedDisplay = mutation.display) }
        }
}

private fun PersonState.copyFeed(feedCopy: FeedState.() -> FeedState): PersonState =
    copy(feedState = feedState.feedCopy())
