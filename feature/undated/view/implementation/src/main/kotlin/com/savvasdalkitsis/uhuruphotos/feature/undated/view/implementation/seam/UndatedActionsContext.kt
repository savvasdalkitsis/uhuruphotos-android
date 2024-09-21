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
package com.savvasdalkitsis.uhuruphotos.feature.undated.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.undated.domain.api.usecase.UndatedUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simpleOk
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UndatedActionsContext @Inject constructor(
    undatedUseCase: UndatedUseCase,
    feedUseCase: FeedUseCase,
    galleryActionsContextFactory: GalleryActionsContextFactory,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = {
            feedUseCase.scheduleFeedRefreshNow()
            delay(500)
            simpleOk
        },
        galleryDetailsFlow = { _ ->
            feedUseCase.observeFeed(FeedFetchType.ONLY_WITHOUT_DATES)
                .map {
                    GalleryDetails(
                        title = Title.Resource(string.media_without_date),
                        clusters = it.map { collection -> collection.toCluster() }
                    )
                }
        },
        shouldRefreshOnLoad = { false },
        lightboxSequenceDataSource = { LightboxSequenceDataSource.Undated },
        initialCollageDisplay = { undatedUseCase.getUndatedGalleryDisplay() },
        collageDisplayPersistence = { _, galleryDisplay ->
            undatedUseCase.setUndatedGalleryDisplay(galleryDisplay)
        },
        shouldShowSortingAction = false,
    )
}
