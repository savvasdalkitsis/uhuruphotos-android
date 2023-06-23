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

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.effects.ErrorLoadingPhotoDetails
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.effects.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.onErrors
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data object Load : HeatMapAction() {

    private val detailsDownloading = MutableStateFlow(false)

    context(HeatMapActionsContext) override fun handle(
        state: HeatMapState,
        effect: EffectHandler<HeatMapEffect>
    ) = merge(
        feedUseCase.observeFeed()
            .map { mediaCollections ->
                mediaCollections
                    .flatMap { it.mediaItems }
                    .filter { it.latLng != null }
            }
            .safelyOnStart {
                feedUseCase.observeFeed().collect { mediaCollections ->
                    detailsDownloading.emit(true)
                    mediaCollections
                        .flatMap { it.mediaItems }
                        .map { mediaItem ->
                            CoroutineScope(currentCoroutineContext() + Dispatchers.IO).async {
                                mediaUseCase.refreshDetailsNowIfMissing(mediaItem.id)
                            }
                        }
                        .awaitAll()
                    detailsDownloading.emit(false)
                }
            }
            .debounce(500)
            .distinctUntilChanged()
            .onErrors { effect.handleEffect(ErrorLoadingPhotoDetails) }
            .flatMapLatest { media ->
                flowOf(HeatMapMutation.UpdateAllMedia(media), updateDisplay(media))
            },
        detailsDownloading
            .map(HeatMapMutation::ShowLoading),
        mediaUseCase.observeLocalMedia()
            .mapNotNull {
                when (it) {
                    is MediaItemsOnDevice.RequiresPermissions -> HeatMapMutation.ShowLocalStoragePermissionRequest(it)
                        .takeIf {
                        settingsUseCase.getShowBannerAskingForLocalMediaPermissionsOnHeatmap()
                    }
                    else -> {
                        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
                        HeatMapMutation.HideLocalStoragePermissionRequest
                    }
                }
            },
    )

}
