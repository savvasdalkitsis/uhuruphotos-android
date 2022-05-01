package com.savvasdalkitsis.uhuruphotos.people.viewmodel

import com.savvasdalkitsis.uhuruphotos.people.view.state.PeopleState
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.toPerson
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer
import javax.inject.Inject

class PeopleReducer @Inject constructor(
    private val photosUseCase: PhotosUseCase,
) : Reducer<PeopleState, PeopleMutation> {
    override suspend fun invoke(
        state: PeopleState,
        mutation: PeopleMutation,
    ): PeopleState =
        when (mutation) {
            is PeopleMutation.DisplayPeople -> with (photosUseCase) {
                state.copy(people = mutation.people.map { it.toPerson { url -> url.toAbsoluteUrl() } })
            }
            is PeopleMutation.SetSortOrder -> state.copy(sortOrder = mutation.sortOrder)
        }
}