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
package com.savvasdalkitsis.uhuruphotos.photos.service.model

import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoResult

private val PhotoResult.serializePeople: String?
    get() = peopleNames?.joinToString(separator = "::")

val String.deserializePeopleNames: List<String>
    get() = split("::")

fun PhotoResult.toPhotoDetails() = PhotoDetails(
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
    peopleNames = serializePeople
)