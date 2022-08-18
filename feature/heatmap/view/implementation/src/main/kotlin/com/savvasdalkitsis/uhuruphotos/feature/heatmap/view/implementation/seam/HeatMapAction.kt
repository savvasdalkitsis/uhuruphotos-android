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

import androidx.compose.ui.geometry.Offset
import com.google.accompanist.permissions.PermissionState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.ui.MapViewState
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem

sealed class HeatMapAction {
    data class CameraViewPortChanged(val boundsChecker: suspend (LatLon) -> Boolean) : HeatMapAction()
    data class SelectedPhoto(
        val mediaItem: MediaItem,
        val center: Offset,
        val scale: Float,
    ) : HeatMapAction()
    data class MyLocationPressed(
        val locationPermissionState: PermissionState,
        val mapViewState: MapViewState
    ) : HeatMapAction()

    object Load : HeatMapAction()
    object BackPressed : HeatMapAction()
}
