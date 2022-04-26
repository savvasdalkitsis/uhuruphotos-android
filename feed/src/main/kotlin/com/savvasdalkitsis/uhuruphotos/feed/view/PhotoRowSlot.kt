package com.savvasdalkitsis.uhuruphotos.feed.view

import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class PhotoRowSlot {
    data class PhotoSlot(val photo: Photo): PhotoRowSlot()
    object EmptySlot : PhotoRowSlot()
}