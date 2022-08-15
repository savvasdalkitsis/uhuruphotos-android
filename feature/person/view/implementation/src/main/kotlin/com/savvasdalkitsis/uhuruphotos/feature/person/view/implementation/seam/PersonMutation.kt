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

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class PersonMutation(
    mutation: Mutation<PersonState>,
) : Mutation<PersonState> by mutation {

    object Loading : PersonMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    data class ShowPersonPhotos(val albums: List<Album>) : PersonMutation({
        it.copyFeed { copy(
            isLoading = false,
            albums = albums,
        ) }
    })

    data class ShowPersonDetails(val person: Person) : PersonMutation({
        it.copy(person = person)
    })

    data class SetFeedDisplay(val display: GalleryDisplay) : PersonMutation({
        it.copyFeed { copy(galleryDisplay = display) }
    })
}

private fun PersonState.copyFeed(feedCopy: GalleryState.() -> GalleryState): PersonState =
    copy(galleryState = galleryState.feedCopy())