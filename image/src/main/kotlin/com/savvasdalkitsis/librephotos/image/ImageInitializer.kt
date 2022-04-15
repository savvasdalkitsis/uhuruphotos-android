package com.savvasdalkitsis.librephotos.image

import coil.Coil
import coil.ImageLoader
import javax.inject.Inject

class ImageInitializer @Inject constructor(
    private val imageLoader: ImageLoader,
) {

    fun initialize() {
        Coil.setImageLoader(imageLoader)
    }
}