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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMediaItem(
    @SerialName("exif_gps_lat") val gpsLat: String?,
    @SerialName("exif_gps_lon") val gpsLon: String?,
    @SerialName("exif_timestamp") val timestamp: String?,
    @SerialName("search_captions") val captions: String?,
    @SerialName("search_location") val location: String?,
    @SerialName("thumbnail_url") val thumbnailUrl: String?,
    @SerialName("thumbnail_height") val thumbnailHeight: String?,
    @SerialName("thumbnail_width") val thumbnailWidth: String?,
    @SerialName("big_thumbnail_url") val bigThumbnailUrl: String?,
    @SerialName("small_thumbnail_url") val smallThumbnailUrl: String?,
    @SerialName("square_thumbnail_url") val squareThumbnailUrl: String?,
    @SerialName("big_square_thumbnail_url") val bigSquareThumbnailUrl: String?,
    @SerialName("small_square_thumbnail_url") val smallSquareThumbnailUrl: String?,
    @SerialName("tiny_square_thumbnail_url") val tinySquareThumbnailUrl: String?,
    @SerialName("image_hash") val imageHash: String,
    @SerialName("image_path") val imagePath: List<String>?,
    @SerialName("video") val video: Boolean,
    @SerialName("rating") val rating: Int,
    @SerialName("people") val people: List<RemoteMediaItemPeople>?,
    @SerialName("size") val size: Long?,
    @SerialName("height") val height: Int?,
    @SerialName("width") val width: Int?,
    @SerialName("focal_length") val focalLength: Double?,
    @SerialName("fstop") val fStop: Double?,
    @SerialName("iso") val iso: String?,
    @SerialName("shutter_speed") val shutterSpeed: String?,
    @SerialName("lens") val lens: String?,
    @SerialName("camera") val camera: String?,
    @SerialName("focalLength35Equivalent") val focalLength35Equivalent: String?,
    @SerialName("digitalZoomRatio") val digitalZoomRatio: String?,
    @SerialName("subjectDistance") val subjectDistance: String?,
)

private val RemoteMediaItem.serializePeople: String?
    get() = people?.joinToString(separator = "::") { it.name }

val String.deserializePeopleNames: List<String>
    get() = split("::")

private val RemoteMediaItem.serializePaths: String?
    get() = imagePath?.serializePaths

val Collection<String>.serializePaths: String
    get() = joinToString(separator = "::") { it }

val String?.deserializePaths: Set<String> @JvmName("get-null-deserializePaths") get() =
    orEmpty().deserializePaths

val String.deserializePaths: Set<String>
    get() = split("::").filter { it.isNotEmpty() }.toSet()

fun RemoteMediaItem.toDbModel() = DbRemoteMediaItemDetails(
    imageHash = imageHash,
    gpsLat = gpsLat,
    gpsLon = gpsLon,
    timestamp = timestamp,
    captions = captions,
    location = location,
    thumbnailHeight = thumbnailHeight,
    thumbnailUrl = thumbnailUrl,
    thumbnailWidth = thumbnailWidth,
    bigThumbnailUrl = bigThumbnailUrl,
    bigSquareThumbnailUrl = bigSquareThumbnailUrl,
    smallSquareThumbnailUrl = smallSquareThumbnailUrl,
    smallThumbnailUrl = smallThumbnailUrl,
    squareThumbnailUrl = smallSquareThumbnailUrl,
    tinySquareThumbnailUrl = tinySquareThumbnailUrl,
    video = video,
    rating = rating,
    peopleNames = serializePeople,
    imagePath = serializePaths,
)