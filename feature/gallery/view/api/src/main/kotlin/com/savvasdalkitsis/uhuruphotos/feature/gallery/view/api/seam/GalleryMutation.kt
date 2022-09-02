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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class GalleryMutation(
    mutation: Mutation<GalleryState>,
) : Mutation<GalleryState> by mutation {

    data class ShowGallery(val galleryDetails: GalleryDetails) : GalleryMutation({
        it.copy(
            title = galleryDetails.title,
            people = galleryDetails.people,
            collageState = it.collageState.copy(
                clusters = galleryDetails.albums.map(Album::toCluster),
            )
        )
    })

    data class Loading(val loading: Boolean) : GalleryMutation({
        it.copy(collageState = it.collageState.copy(
            isLoading = loading,
            isEmpty = !loading && !it.collageState.hasMedia
        ))
    })

    data class ChangeCollageDisplay(val collageDisplay: CollageDisplay) : GalleryMutation({
        it.copy(collageState = it.collageState.copy(
            collageDisplay = collageDisplay,
        ))
    })
}
