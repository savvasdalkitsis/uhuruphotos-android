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
package com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaDetails
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class GalleriaMutation(
    mutation: Mutation<GalleriaState>,
) : Mutation<GalleriaState> by mutation {

    data class ShowGalleria(val galleriaDetails: GalleriaDetails) : GalleriaMutation({
        it.copy(
            title = galleriaDetails.title,
            people = galleriaDetails.people,
            galleryState = it.galleryState.copy(
                albums = galleriaDetails.albums,
            )
        )
    })

    data class Loading(val loading: Boolean) : GalleriaMutation({
        it.copy(galleryState = it.galleryState.copy(
            isLoading = loading,
            isEmpty = !loading && !it.galleryState.hasMedia
        ))
    })

    data class ChangeGalleryDisplay(val galleryDisplay: GalleryDisplay) : GalleriaMutation({
        it.copy(galleryState = it.galleryState.copy(
            galleryDisplay = galleryDisplay,
        ))
    })
}
