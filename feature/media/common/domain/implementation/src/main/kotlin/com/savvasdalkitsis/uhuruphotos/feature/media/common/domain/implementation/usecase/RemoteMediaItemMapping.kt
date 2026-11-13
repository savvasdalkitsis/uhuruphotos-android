/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDayModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel

internal fun DbRemoteMediaItemSummary.toRemoteMediaItem(
    userId: Int?,
    favouriteThreshold: Int?,
    displayDate: String?,
    mediaDay: MediaDayModel?,
) = remoteMediaItem(id, url, isVideo, userId).copy(
    fallbackColor = dominantColor,
    displayDayDate = displayDate,
    sortableDate = date,
    isFavourite = isFavourite(rating, favouriteThreshold),
    ratio = aspectRatio.orSquare(),
    mediaDay = mediaDay,
)

internal fun MediaCollectionSourceModel.toRemoteMediaItem(
    userId: Int?,
    favouriteThreshold: Int?,
    displayDate: String?,
    mediaDay: MediaDayModel?,
): MediaItemInstanceModel? = mediaItemId?.takeIf { it.isNotBlank() }?.let { id ->
    remoteMediaItem(id, mediaItemHash, isVideo, userId).copy(
        fallbackColor = dominantColor,
        displayDayDate = displayDate,
        sortableDate = date,
        isFavourite = isFavourite(rating, favouriteThreshold),
        ratio = aspectRatio.orSquare(),
        latLng = latLng(lat, lon),
        mediaDay = mediaDay,
    )
}

internal fun remoteMediaItem(
    id: String,
    contentHash: String?,
    isVideo: Boolean,
    userId: Int?,
): MediaItemInstanceModel {
    val mediaHash = MediaItemHashModel.fromRemoteMediaHash(contentHash ?: id, userId)
    return MediaItemInstanceModel(
        id = RemoteIdModel(id, isVideo, mediaHash),
        mediaHash = mediaHash,
    )
}

private fun isFavourite(rating: Int?, favouriteThreshold: Int?) =
    favouriteThreshold != null && (rating ?: 0) >= favouriteThreshold

private fun latLng(lat: String?, lon: String?): Pair<Double, Double>? {
    val latitude = lat?.toDoubleOrNull() ?: return null
    val longitude = lon?.toDoubleOrNull() ?: return null
    return latitude to longitude
}
