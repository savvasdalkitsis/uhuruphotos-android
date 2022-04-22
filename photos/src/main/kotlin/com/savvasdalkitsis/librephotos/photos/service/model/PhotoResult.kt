package com.savvasdalkitsis.librephotos.photos.service.model

import com.savvasdalkitsis.librephotos.db.photos.PhotoDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoResult(
    @field:Json(name = "exif_gps_lat") val gpsLat: String?,
    @field:Json(name = "exif_gps_lon") val gpsLon: String?,
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
    @field:Json(name = "video") val video: Boolean,
    @field:Json(name = "rating") val rating: Int,
)

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
    albumId = "",
)