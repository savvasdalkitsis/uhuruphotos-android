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
package com.savvasdalkitsis.uhuruphotos.heatmap.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.api.coroutines.onErrors
import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapAction.BackPressed
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapAction.CameraViewPortChanged
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapAction.Load
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapEffect.ErrorLoadingPhotoDetails
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapEffect.NavigateToPhoto
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapMutation.ShowLoading
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapMutation.UpdateAllPhotos
import com.savvasdalkitsis.uhuruphotos.heatmap.seam.HeatMapMutation.UpdateDisplay
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.model.latLng
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class HeatMapHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val photosUseCase: PhotosUseCase,
): ActionHandler<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapMutation> {

    private var boundsChecker: suspend (LatLon) -> Boolean = { true }
    private val detailsDownloading = MutableStateFlow(false)

    override fun handleAction(
        state: HeatMapState,
        action: HeatMapAction,
        effect: suspend (HeatMapEffect) -> Unit
    ): Flow<HeatMapMutation> = when (action) {
        Load -> merge(
            photosUseCase.observeAllPhotoDetails()
                .map { photos ->
                    photos
                        .filter { it.latLng != null }
                        .map {
                            Photo(
                                id = it.imageHash,
                                thumbnailUrl = with(photosUseCase) {
                                    it.imageHash.toThumbnailUrlFromId()
                                },
                                latLng = it.latLng?.let { latLng ->
                                    latLng.lat to latLng.lon
                                },
                                isVideo = it.video ?: false,
                            )
                        }
                }
                .safelyOnStart {
                    albumsUseCase.observeAlbums().collect { albums ->
                        detailsDownloading.emit(true)
                        albums
                            .flatMap { it.photos }
                            .map { photo ->
                                CoroutineScope(currentCoroutineContext() + Dispatchers.IO).async {
                                    photosUseCase.refreshDetailsNowIfMissing(photo.id)
                                }
                            }
                            .awaitAll()
                        detailsDownloading.emit(false)
                    }
                }
                .debounce(500)
                .distinctUntilChanged()
                .onErrors { effect(ErrorLoadingPhotoDetails) }
                .flatMapLatest { photos ->
                    flowOf(UpdateAllPhotos(photos), updateDisplay(photos))
                },
            detailsDownloading
                .map(::ShowLoading)
        )
        BackPressed -> flow {
            effect(NavigateBack)
        }
        is CameraViewPortChanged -> flow {
            boundsChecker = action.boundsChecker
            emit(updateDisplay(state.allPhotos))
        }
        is SelectedPhoto -> flow {
            effect(with(action) {
                NavigateToPhoto(photo, center, scale)
            })
        }
    }

    private suspend fun updateDisplay(allPhotos: List<Photo>): UpdateDisplay {
        val photosToDisplay = allPhotos
            .filter { photo ->
                val latLon = photo.latLng.toLatLon()
                latLon != null && boundsChecker(latLon)
            }
        val pointsToDisplay = photosToDisplay
            .mapNotNull { it.latLng.toLatLon() }
        return UpdateDisplay(photosToDisplay, pointsToDisplay)
    }

}

private fun Pair<Double, Double>?.toLatLon() =
    this?.let { (lat, lon) -> LatLon(lat, lon) }
