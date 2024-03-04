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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.ExifData
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon

data class LightboxDetails(
    val formattedDateTime: String? = null,
    val location: String? = null,
    val latLon: LatLon? = null,
    val remotePaths: Set<String> = emptySet(),
    val localPaths: Set<String> = emptySet(),
    val hash: MediaItemHash? = null,
    val peopleInMediaItem: List<People>,
    val searchCaptions: Set<String> = emptySet(),
    val size: String? = null,
    val exifData: ExifData,
) {

    fun mergeWith(other: LightboxDetails): LightboxDetails = copy(
        formattedDateTime = formattedDateTime ?: other.formattedDateTime,
        location = location.orEmpty().ifBlank { other.location },
        latLon = latLon ?: other.latLon,
        remotePaths = remotePaths + other.remotePaths,
        localPaths = localPaths + other.localPaths,
        hash = hash ?: other.hash,
        peopleInMediaItem = peopleInMediaItem.ifEmpty { other.peopleInMediaItem },
        searchCaptions = searchCaptions + other.searchCaptions,
        size = size ?: other.size,
        exifData = exifData.mergeWith(other.exifData)
    )
}