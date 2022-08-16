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
package com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomDetails
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class ShowroomMutation(
    mutation: Mutation<ShowroomState>,
) : Mutation<ShowroomState> by mutation {

    data class ShowShowroom(val showroomDetails: ShowroomDetails) : ShowroomMutation({
        it.copy(
            title = showroomDetails.title,
            people = showroomDetails.people,
            galleryState = it.galleryState.copy(
                albums = showroomDetails.albums,
            )
        )
    })

    data class Loading(val loading: Boolean) : ShowroomMutation({
        it.copy(galleryState = it.galleryState.copy(
            isLoading = loading,
            isEmpty = !loading && !it.galleryState.hasMedia
        ))
    })

    data class ChangeGalleryDisplay(val galleryDisplay: GalleryDisplay) : ShowroomMutation({
        it.copy(galleryState = it.galleryState.copy(
            galleryDisplay = galleryDisplay,
        ))
    })
}
