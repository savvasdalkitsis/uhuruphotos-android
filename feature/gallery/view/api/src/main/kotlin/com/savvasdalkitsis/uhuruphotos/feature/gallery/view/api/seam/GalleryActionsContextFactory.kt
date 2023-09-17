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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import dagger.assisted.AssistedFactory
import kotlinx.coroutines.flow.Flow

@AssistedFactory
interface GalleryActionsContextFactory {

    fun create(
        galleryRefresher: suspend (Int) -> SimpleResult,
        galleryDetailsFlow: (galleryId: Int) -> Flow<GalleryDetails>,
        shouldRefreshOnLoad: suspend (galleryId: Int) -> Boolean,
        lightboxSequenceDataSource: (galleryId: Int) -> LightboxSequenceDataSource,
        initialCollageDisplay: (galleryId: Int) -> CollageDisplay,
        collageDisplayPersistence: suspend (galleryId: Int, PredefinedCollageDisplay) -> Unit,
        shouldShowSortingAction: Boolean = true,
    ): GalleryActionsContext
}
