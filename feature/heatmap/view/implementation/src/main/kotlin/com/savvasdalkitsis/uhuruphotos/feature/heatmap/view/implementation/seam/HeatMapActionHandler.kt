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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam

import android.annotation.SuppressLint
import android.location.LocationManager
import android.location.LocationManager.NETWORK_PROVIDER
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.accompanist.permissions.isGranted
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapAction.*
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect.*
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState.*
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.onErrors
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import java.lang.Runnable
import javax.inject.Inject

class HeatMapActionHandler @Inject constructor(
    private val feedUseCase: FeedUseCase,
    private val mediaUseCase: MediaUseCase,
    private val settingsUseCase: SettingsUseCase,
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
    private val locationManager: LocationManager,
): ActionHandler<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapMutation> {

    @Volatile
    private var boundsChecker: suspend (LatLon) -> Boolean = { true }
    private val detailsDownloading = MutableStateFlow(false)
    private var updateVisibleMapContentJob: Deferred<UpdateVisibleMapContent>? = null

    @SuppressLint("MissingPermission")
    override fun handleAction(
        state: HeatMapState,
        action: HeatMapAction,
        effect: suspend (HeatMapEffect) -> Unit
    ): Flow<HeatMapMutation> = when (action) {
        Load -> merge(
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
                .onErrors { effect(ErrorLoadingPhotoDetails) }
                .flatMapLatest { media ->
                    flowOf(UpdateAllMedia(media), updateDisplay(media))
                },
            detailsDownloading
                .map(::ShowLoading),
            mediaUseCase.observeLocalMedia()
                .mapNotNull {
                    when (it) {
                        is MediaItemsOnDevice.RequiresPermissions -> ShowLocalStoragePermissionRequest(it).takeIf {
                            settingsUseCase.getShowBannerAskingForLocalMediaPermissionsOnHeatmap()
                        }
                        else -> {
                            localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
                            HideLocalStoragePermissionRequest
                        }
                    }
                },
        )
        NeverAskForLocalMediaAccessPermissionRequest -> flow {
            emit(HideLocalStoragePermissionRequest)
            settingsUseCase.setShowBannerAskingForLocalMediaPermissionsOnHeatmap(false)
        }
        BackPressed -> flow {
            effect(NavigateBack)
        }
        is CameraViewPortChanged -> flow {
            updateVisibleMapContentJob?.cancel()
            boundsChecker = action.boundsChecker
            updateVisibleMapContentJob = CoroutineScope(currentCoroutineContext()).async { updateDisplay(state.allMedia) }
            updateVisibleMapContentJob?.await()?.let {
                emit(it)
            }
        }
        is SelectedCel -> flow {
            effect(with(action) {
                NavigateToLightbox(celState, center, scale)
            })
        }
        is MyLocationPressed -> flow {
            if (action.locationPermissionState.status.isGranted) {
                var location: LatLon? = null
                getCurrentLocation(locationManager, NETWORK_PROVIDER, null, Runnable::run) {
                    location = LatLon(it.latitude, it.longitude)
                }
                location?.let {
                    action.mapViewState.centerToLocation(it)
                }
            } else {
                action.locationPermissionState.launchPermissionRequest()
            }
        }
    }

    private suspend fun updateDisplay(allMedia: List<MediaItem>): UpdateVisibleMapContent {
        val photosToDisplay = allMedia
            .filter { photo ->
                val latLon = photo.latLng.toLatLon()
                latLon != null && boundsChecker(latLon)
            }
        val pointsToDisplay = photosToDisplay
            .mapNotNull { it.latLng.toLatLon() }
        return UpdateVisibleMapContent(photosToDisplay, pointsToDisplay)
    }

}

private fun Pair<Double, Double>?.toLatLon() =
    this?.let { (lat, lon) -> LatLon(lat, lon) }
