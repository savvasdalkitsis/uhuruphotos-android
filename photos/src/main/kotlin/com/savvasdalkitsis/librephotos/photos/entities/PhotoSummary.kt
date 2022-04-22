package com.savvasdalkitsis.librephotos.photos.entities

import com.savvasdalkitsis.librephotos.photos.service.model.PhotoSummaryItem
import com.savvasdalkitsis.librephotos.db.photos.PhotoSummary

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