package com.savvasdalkitsis.uhuruphotos.feed.view

import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

sealed class PhotoSelectionMode {
    object Disabled: PhotoSelectionMode()
    class Multiple(
        val onItemSelectionChanged: (selectedPhotos: List<Photo>) -> Unit,
    ) : PhotoSelectionMode()

}
