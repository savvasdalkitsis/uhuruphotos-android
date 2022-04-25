package com.savvasdalkitsis.uhuruphotos.photos.entities

import com.savvasdalkitsis.uhuruphotos.photos.service.model.PhotoSummaryItem
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoSummary

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