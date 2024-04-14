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

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.Viewport
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.collections.immutable.toPersistentList

sealed class HeatMapMutation(
    mutation: Mutation<HeatMapState>,
) : Mutation<HeatMapState> by mutation {

    data class UpdateAllMedia(val mediaItems: List<MediaItem>) : HeatMapMutation({
        val points = mediaItems
            .mapNotNull { photo -> photo.latLng }
            .map { (lat, lon) -> LatLon(lat, lon) }
        it.copy(
            allPoints = points.toPersistentList(),
            pointsOnVisibleMap = points.toPersistentList(),
            allMedia = mediaItems.toPersistentList(),
        )
    })

    data class UpdateVisibleMapContent(
        val photosOnVisibleMap: List<MediaItem>,
        val pointsOnVisibleMap: List<LatLon>,
    ) : HeatMapMutation({
        it.copy(
            photosOnVisibleMap = photosOnVisibleMap.map(MediaItem::toCel).toPersistentList(),
            pointsOnVisibleMap = pointsOnVisibleMap.toPersistentList(),
        )
    })

    data class ShowLoading(val loading: Boolean) : HeatMapMutation({
        it.copy(loading = loading)
    })

    data class ShowLocalStoragePermissionRequest(val permissions: MediaItemsOnDevice.RequiresPermissions) : HeatMapMutation({
        it.copy(showRequestPermissionForLocalMediaAccess = permissions)
    })

    data class ChangeInitialMapViewPort(val initialViewPort: Viewport) : HeatMapMutation({
        it.copy(initialViewport = initialViewPort)
    })

    data object HideLocalStoragePermissionRequest : HeatMapMutation({
        it.copy(showRequestPermissionForLocalMediaAccess = null)
    })
}
