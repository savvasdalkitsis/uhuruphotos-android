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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GallerySortingState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toPersistentList

sealed class GalleryMutation(
    mutation: Mutation<GalleryState>,
) : Mutation<GalleryState> by mutation {

    data class ShowGallery(val galleryDetailsState: GalleryDetailsState) : GalleryMutation({
        it.copy(
            title = galleryDetailsState.title,
            people = galleryDetailsState.people.toPersistentList(),
            collageState = it.collageState.copy(
                isEmpty = galleryDetailsState.clusterStates.isEmpty(),
                clusterStates = galleryDetailsState.clusterStates.toPersistentList(),
            )
        )
    })

    data class Loading(val loading: Boolean) : GalleryMutation({
        it.copy(collageState = it.collageState.copy(
            isLoading = loading,
            isEmpty = !loading && !it.collageState.hasMedia
        ))
    })

    data class ChangeCollageDisplay(val collageDisplayState: CollageDisplayState) : GalleryMutation({
        it.copy(collageState = it.collageState.copy(
            collageDisplayState = collageDisplayState,
        ))
    })

    data class ShowGallerySorting(val sorting: GallerySortingState?) : GalleryMutation({
        it.copy(sorting = sorting)
    })
}
