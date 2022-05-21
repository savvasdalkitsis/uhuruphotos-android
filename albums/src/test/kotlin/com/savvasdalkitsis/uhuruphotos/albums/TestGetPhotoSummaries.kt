package com.savvasdalkitsis.uhuruphotos.albums

import com.savvasdalkitsis.uhuruphotos.photos.TestPhotoSummaries.*
import com.savvasdalkitsis.uhuruphotos.photos.TestPhotos.*

fun photoId(id: Int) = "photo$id"
fun photo(id: Int) = photoSummary.copy(id = photoId(id))
fun photo(id: Int, inAlbum: Int) = photoSummary.copy(id = photoId(id), containerId = albumId(inAlbum))
fun photoSummaryItem(id: Int) = photoSummaryItem.copy(id = photoId(id))