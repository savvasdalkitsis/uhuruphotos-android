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
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMediaItem(
    @field:Json(name = "exif_timestamp") val timestamp: String?,
    @field:Json(name = "search_captions") val captions: String?,
    @field:Json(name = "search_location") val location: String?,
    @field:Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @field:Json(name = "thumbnail_height") val thumbnailHeight: String?,
    @field:Json(name = "thumbnail_width") val thumbnailWidth: String?,
    @field:Json(name = "big_thumbnail_url") val bigThumbnailUrl: String?,
    @field:Json(name = "small_thumbnail_url") val smallThumbnailUrl: String?,
    @field:Json(name = "square_thumbnail_url") val squareThumbnailUrl: String?,
    @field:Json(name = "big_square_thumbnail_url") val bigSquareThumbnailUrl: String?,
    @field:Json(name = "small_square_thumbnail_url") val smallSquareThumbnailUrl: String?,
    @field:Json(name = "tiny_square_thumbnail_url") val tinySquareThumbnailUrl: String?,
    @field:Json(name = "image_hash") val imageHash: String,
    @field:Json(name = "image_path") val imagePath: List<String>?,
    @field:Json(name = "video") val video: Boolean,
    @field:Json(name = "rating") val rating: Int,
    @field:Json(name = "people") val people: List<RemoteMediaItemPeople>?,
    @field:Json(name = "size") val size: Long?,
    @field:Json(name = "height") val height: Int?,
    @field:Json(name = "width") val width: Int?,
    @field:Json(name = "focal_length") val focalLength: Double?,
    @field:Json(name = "fstop") val fStop: Double?,
    @field:Json(name = "iso") val iso: String?,
    @field:Json(name = "shutter_speed") val shutterSpeed: String?,
    @field:Json(name = "lens") val lens: String?,
    @field:Json(name = "camera") val camera: String?,
    @field:Json(name = "focalLength35Equivalent") val focalLength35Equivalent: String?,
    @field:Json(name = "digitalZoomRatio") val digitalZoomRatio: String?,
    @field:Json(name = "subjectDistance") val subjectDistance: String?,
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