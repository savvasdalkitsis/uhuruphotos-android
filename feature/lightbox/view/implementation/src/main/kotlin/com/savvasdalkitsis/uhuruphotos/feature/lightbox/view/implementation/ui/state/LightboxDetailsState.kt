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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model.LightboxDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet

data class LightboxDetailsState(
    val formattedDateTime: String? = null,
    val location: String? = null,
    val latLon: LatLon? = null,
    val remotePaths: ImmutableSet<String> = persistentSetOf(),
    val localPaths: ImmutableSet<String> = persistentSetOf(),
    val hash: MediaItemHash? = null,
    val peopleInMediaItem: ImmutableList<Person> = persistentListOf(),
    val searchCaptions: ImmutableSet<String> = persistentSetOf(),
    val size: String? = null,
    val fStop: String? = null,
    val shutterSpeed: String? = null,
    val isoSpeed: String? = null,
    val camera: String? = null,
    val focalLength: String? = null,
    val focalLength35Equivalent: String? = null,
    val subjectDistance: String? = null,
    val digitalZoomRatio: String? = null,
    val width: Int? = null,
    val height: Int? = null,
) {

    val megapixels: String? = whSafe { w, h ->
            "${(w * h.toLong()).mb.round(2)} MP"
        }
    val wh: Pair<Int, Int>? = whSafe { w, h ->
            w to h
        }

    val isEmpty = localPaths.isEmpty() && remotePaths.isEmpty() && size.isNullOrBlank() &&
            fStop.isNullOrBlank() &&
            shutterSpeed.isNullOrBlank() &&
            isoSpeed.isNullOrBlank() &&
            camera.isNullOrBlank() &&
            focalLength.isNullOrBlank() &&
            focalLength35Equivalent.isNullOrBlank() &&
            subjectDistance.isNullOrBlank() &&
            digitalZoomRatio.isNullOrBlank() &&
            width == null &&
            height == null

    private fun <T> whSafe(action: (Int, Int) -> T): T? =
        if (width != null && width != 0 && height != null && height != 0) {
            action(width, height)
        } else {
            null
        }
}

fun LightboxDetails.toLightboxDetailsState(serverUrl: String) = LightboxDetailsState(
        formattedDateTime = formattedDateTime,
        location = location,
        latLon = latLon,
        remotePaths = remotePaths.toPersistentSet(),
        localPaths = localPaths.toPersistentSet(),
        hash = hash,
        peopleInMediaItem = peopleInMediaItem
            .map {
                it.toPerson { url ->
                    "$serverUrl$url"
                }
            }.toPersistentList(),
        searchCaptions = searchCaptions.toPersistentSet(),
        size = size,
        fStop = exifData.fStop,
        shutterSpeed = exifData.shutterSpeed,
        isoSpeed = exifData.isoSpeed,
        camera = exifData.camera,
        focalLength = exifData.focalLength,
        focalLength35Equivalent = exifData.focalLength35Equivalent,
        subjectDistance = exifData.subjectDistance,
        digitalZoomRatio = exifData.digitalZoomRatio,
        width = exifData.width,
        height = exifData.height,
    )

private val Long.mb get() = this / 1024 / 1024f
private fun Float.round(decimals: Int = 2): String = "%.${decimals}f".format(this)