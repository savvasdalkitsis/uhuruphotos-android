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
package com.savvasdalkitsis.uhuruphotos.heatmap.view.state

import com.savvasdalkitsis.uhuruphotos.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo

data class HeatMapState(
    val loading: Boolean = false,
    val pointsToDisplay: List<LatLon> = emptyList(),
    val allPhotos: List<Photo> = emptyList(),
    val photosToDisplay: List<Photo> = emptyList(),
)
