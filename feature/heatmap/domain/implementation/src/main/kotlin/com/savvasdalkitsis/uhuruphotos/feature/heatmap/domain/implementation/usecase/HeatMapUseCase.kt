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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.domain.api.usecase.HeatMapUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.Locations.TRAFALGAR_SQUARE
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.toLatLon
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class HeatMapUseCase @Inject constructor(
    private val preferences: Preferences,
) : HeatMapUseCase {

    private val keyLat = "heatmapCenterLat"
    private val keyLon = "heatmapCenterLon"
    private val keyZoom = "heatmapZoom"

    override fun observeViewport(): Flow<Viewport> =
        combine(
            preferences.observe(keyLat, TRAFALGAR_SQUARE.center.lat),
            preferences.observe(keyLon, TRAFALGAR_SQUARE.center.lon),
            preferences.observe(keyZoom, 3f),
        ) { lat, lon, zoom ->
            Viewport((lat to lon).toLatLon, zoom)
        }

    override fun setViewport(viewport: Viewport) {
        with(preferences) {
            set(keyLat, viewport.center.lat)
            set(keyLon, viewport.center.lon)
            set(keyZoom, viewport.zoom)
        }
    }
}