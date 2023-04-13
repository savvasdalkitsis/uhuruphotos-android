package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation
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

object Load : HeatMapAction() {

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
                                mediaUseCase.refreshDetailsNowIfMissing(mediaItem.id, mediaItem.isVideo)
                            }
                        }
                        .awaitAll()
                    detailsDownloading.emit(false)
                }
            }
            .debounce(500)
            .distinctUntilChanged()
            .onErrors { effect.handleEffect(HeatMapEffect.ErrorLoadingPhotoDetails) }
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