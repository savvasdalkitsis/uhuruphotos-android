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