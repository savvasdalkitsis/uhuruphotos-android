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
package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapAction.*
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapEffect.*
import com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel.HeatMapMutation.*
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.onErrors
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo
import com.savvasdalkitsis.uhuruphotos.photos.model.latLng
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class HeatMapHandler @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
    private val photosUseCase: PhotosUseCase,
): Handler<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapMutation> {

    private var boundsChecker: suspend (LatLng) -> Boolean = { true }
    private val detailsDownloading = MutableStateFlow(false)

    override fun invoke(
        state: HeatMapState,
        action: HeatMapAction,
        effect: suspend (HeatMapEffect) -> Unit
    ): Flow<HeatMapMutation> = when (action) {
        Load -> merge(
            photosUseCase.getAllPhotos()
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
                                    latLng.latitude to latLng.longitude
                                },
                                isVideo = it.video ?: false,
                            )
                        }
                }
                .safelyOnStart {
                    albumsUseCase.getAlbums().collect { albums ->
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
                val latLng = photo.latLng.toLatLng()
                latLng != null && boundsChecker(latLng)
            }
        val pointsToDisplay = photosToDisplay
            .mapNotNull { it.latLng.toLatLng() }
        return UpdateDisplay(photosToDisplay, pointsToDisplay)
    }

}

private fun Pair<Double, Double>?.toLatLng() =
    this?.let { (lat, lon) -> LatLng(lat, lon) }
