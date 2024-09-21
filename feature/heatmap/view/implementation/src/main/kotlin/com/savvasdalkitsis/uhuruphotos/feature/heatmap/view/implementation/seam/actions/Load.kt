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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data object Load : HeatMapAction() {

    context(HeatMapActionsContext) override fun handle(
        state: HeatMapState
    ) = merge(
        flow {
            val initialViewPort = heatMapUseCase.observeViewport().first()
            emit(HeatMapMutation.ChangeInitialMapViewPort(initialViewPort))
        },
        feedUseCase.observeFeed(FeedFetchType.ALL)
            .map { mediaCollections ->
                mediaCollections
                    .flatMap { it.mediaItems }
                    .filter { it.latLng != null }
            }
            .flatMapLatest { media ->
                flowOf(HeatMapMutation.UpdateAllMedia(media), updateDisplay(media))
            },
        mediaUseCase.observeLocalMedia()
            .mapNotNull {
                when (it) {
                    is MediaItemsOnDevice.RequiresPermissions -> HeatMapMutation.ShowLocalStoragePermissionRequest(it)
                        .takeIf {
                        settingsUIUseCase.getShowBannerAskingForLocalMediaPermissionsOnHeatmap()
                    }
                    else -> {
                        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
                        HeatMapMutation.HideLocalStoragePermissionRequest
                    }
                }
            },
    )

}
