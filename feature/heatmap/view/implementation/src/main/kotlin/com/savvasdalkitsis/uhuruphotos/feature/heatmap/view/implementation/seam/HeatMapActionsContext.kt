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
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.usecase.HeatMapUseCase
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation.UpdateVisibleMapContent
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import kotlinx.coroutines.Deferred
import javax.inject.Inject

internal class HeatMapActionsContext @Inject constructor(
    val feedUseCase: FeedUseCase,
    val feedWorkScheduler: FeedWorkScheduler,
    val mediaUseCase: MediaUseCase,
    val settingsUseCase: SettingsUseCase,
    val localMediaWorkScheduler: LocalMediaWorkScheduler,
    val locationManager: LocationManager,
    val heatMapUseCase: HeatMapUseCase,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
) {

    @Volatile
    var boundsChecker: suspend (LatLon) -> Boolean = { true }
    var updateVisibleMapContentJob: Deferred<UpdateVisibleMapContent>? = null

    suspend fun updateDisplay(allMedia: List<MediaItem>): UpdateVisibleMapContent {
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