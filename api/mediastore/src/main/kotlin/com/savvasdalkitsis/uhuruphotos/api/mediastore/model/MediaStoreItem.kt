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
package com.savvasdalkitsis.uhuruphotos.api.mediastore.model

import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo

data class MediaStoreItem(
    val id: Long,
    val displayName: String?,
    val dateTaken: String,
    val bucket: MediaBucket,
    val width: Int,
    val height: Int,
    val size: Int,
    val contentUri: String,
    val md5: String,
    val video: Boolean,
    val duration: Int?,
    val latLon: Pair<Double, Double>?,
    val fallbackColor: String?,
) {

    fun toPhoto(userId: String) = Photo(
        id = md5 + userId,
        thumbnailUrl = contentUri,
        fullResUrl = contentUri,
        fallbackColor = fallbackColor,
        isFavourite = false,
        ratio = (width / height.toFloat()).takeIf { it > 0 } ?: 1f,
        isVideo = video,
        latLng = latLon,
    )
}