package com.savvasdalkitsis.librephotos.photos.db.entities

import com.savvasdalkitsis.librephotos.photos.api.model.PhotoSummaryItem
import com.savvasdalkitsis.librephotos.photos.db.PhotoSummary

fun PhotoSummaryItem.toPhotoSummary(albumId: String) = PhotoSummary(
    id = id,
    dominantColor = dominantColor,
    url = url,
    location = location,
    date = date,
    birthTime = birthTime,
    aspectRatio = aspectRatio,
    type = type,
    videoLength = videoLength,
    rating = rating,
    containerId = albumId
)