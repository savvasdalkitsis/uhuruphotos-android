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

import androidx.compose.ui.geometry.Offset
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class HeatMapAction {
    data class CameraViewPortChanged(val boundsChecker: suspend (LatLng) -> Boolean) : HeatMapAction()
    data class SelectedPhoto(
        val photo: Photo,
        val center: Offset,
        val scale: Float,
    ) : HeatMapAction()
    object Load : HeatMapAction()
    object BackPressed : HeatMapAction()
}
