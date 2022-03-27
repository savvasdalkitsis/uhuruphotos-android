package com.savvasdalkitsis.librephotos.photos.db.entities

import com.savvasdalkitsis.librephotos.photos.api.model.PhotoResult
import com.savvasdalkitsis.librephotos.photos.db.PhotoDetails

fun PhotoResult.toPhotoDetails(albumId: String) = PhotoDetails(
    imageHash = imageHash,
    gpsLat = gpsLat,
    gpsLon = gpsLon,
    timestamp = timestamp,
    captions = captions,
    location = location,
    thumbnailUrl = thumbnailUrl,
    thumbnailHeight = thumbnailHeight,
    thumbnailWidth = thumbnailWidth,
    bigThumbnailUrl = bigThumbnailUrl,
    smallThumbnailUrl = smallThumbnailUrl,
    squareThumbnailUrl = squareThumbnailUrl,
    bigSquareThumbnailUrl = bigSquareThumbnailUrl,
    smallSquareThumbnailUrl = smallSquareThumbnailUrl,
    tinySquareThumbnailUrl = tinySquareThumbnailUrl,
    video = video,
    albumId = albumId
)