package com.savvasdalkitsis.librephotos.photos.usecase

import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import javax.inject.Inject

class PhotosUseCase @Inject constructor(
    private val serverUseCase: ServerUseCase,
) {

    suspend fun String?.toAbsoluteUrl(): String? {
        val serverUrl = serverUseCase.getServerUrl()
        return this
            ?.removeSuffix(".webp")
            ?.removeSuffix(".mp4")
            ?.let { serverUrl + it }
    }

    suspend fun String?.toThumbnailUrlFromId(video: Boolean = false): String? = this?.let {
        when (video) {
            true -> "/media/square_thumbnails/$it"
            else -> "/media/thumbnails_big/$it"
        }.toAbsoluteUrl()
    }

}
