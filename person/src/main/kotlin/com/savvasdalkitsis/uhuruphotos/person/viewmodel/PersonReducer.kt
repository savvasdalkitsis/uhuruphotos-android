package com.savvasdalkitsis.uhuruphotos.person.viewmodel

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.people.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.person.view.state.PersonState
import com.savvasdalkitsis.uhuruphotos.person.viewmodel.PersonMutation.*
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
                state.copy(person = mutation.person.toPerson())
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
