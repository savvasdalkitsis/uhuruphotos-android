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

import android.location.LocationManager
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.usecase.HeatMapUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation.UpdateVisibleMapContent
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class HeatMapActionsContext @Inject constructor(
    val feedUseCase: FeedUseCase,
    val mediaUseCase: MediaUseCase,
    val settingsUIUseCase: SettingsUIUseCase,
    val localMediaWorkScheduler: LocalMediaWorkScheduler,
    val locationManager: LocationManager,
    val heatMapUseCase: HeatMapUseCase,
    val navigator: Navigator,
) {

    @Volatile
    var boundsChecker: suspend (LatLon) -> Boolean = { true }
    private val viewportChanges = MutableStateFlow<ViewportChange?>(null)

    fun changeViewport(change: ViewportChange) {
        viewportChanges.value = change
    }

    fun visibleMapContentChanges(): Flow<UpdateVisibleMapContent> = viewportChanges
        .filterNotNull()
        .mapLatest { change ->
            boundsChecker = change.boundsChecker
            heatMapUseCase.setViewport(change.viewport)
            updateDisplay(change.media)
        }

    suspend fun updateDisplay(allMedia: List<MediaItemModel>): UpdateVisibleMapContent {
        val photosToDisplay = allMedia
            .filter { photo ->
                val latLon = photo.latLng.toLatLon()
                latLon != null && boundsChecker(latLon)
            }
        val pointsToDisplay = photosToDisplay
            .mapNotNull { it.latLng.toLatLon() }
        return UpdateVisibleMapContent(photosToDisplay, pointsToDisplay)
    }

    private fun Pair<Double, Double>?.toLatLon() =
        this?.let { (lat, lon) -> LatLon(lat, lon) }
}