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
package com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam

import com.savvasdalkitsis.uhuruphotos.api.map.model.LatLon
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.view.state.HeatMapState

sealed class HeatMapMutation(
    mutation: Mutation<HeatMapState>,
) : Mutation<HeatMapState> by mutation {

    data class UpdateAllPhotos(val mediaItems: List<MediaItem>) : HeatMapMutation({
        val points = mediaItems
            .mapNotNull { photo -> photo.latLng }
            .map { (lat, lon) -> LatLon(lat, lon) }
        it.copy(
            allPoints = points,
            pointsOnVisibleMap = points,
            allMedia = mediaItems,
        )
    })

    data class UpdateVisibleMapContent(
        val photosOnVisibleMap: List<MediaItem>,
        val pointsOnVisibleMap: List<LatLon>,
    ) : HeatMapMutation({
        it.copy(
            photosOnVisibleMap = photosOnVisibleMap,
            pointsOnVisibleMap = pointsOnVisibleMap,
        )
    })

    data class ShowLoading(val loading: Boolean) : HeatMapMutation({
        it.copy(loading = loading)
    })
}
