/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action

import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import kotlinx.coroutines.flow.flow

data class SelectedCel(
    val cel: CelState,
) : GalleryAction() {
    context(GalleryActionsContext) override fun handle(
        state: GalleryState
    ) = flow<GalleryMutation> {
        navigator.navigateTo(
            LightboxNavigationRoute(
                mediaItem = cel.mediaItem,
                lightboxSequenceDataSource = lightboxSequenceDataSource(galleryId.id),
            )
        )
    }
}
