package com.savvasdalkitsis.librephotos.feed.view

import com.savvasdalkitsis.librephotos.photos.model.Photo

sealed class PhotoSelectionMode {
    object Disabled: PhotoSelectionMode()
    class Multiple(
        val onItemSelectionChanged: (selectedPhotos: List<Photo>) -> Unit,
    ) : PhotoSelectionMode()

}
