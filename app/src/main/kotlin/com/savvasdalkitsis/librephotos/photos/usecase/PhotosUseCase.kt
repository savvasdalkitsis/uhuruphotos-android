package com.savvasdalkitsis.librephotos.photos.usecase

import com.savvasdalkitsis.librephotos.photos.db.PhotoDetails
import com.savvasdalkitsis.librephotos.photos.repository.PhotoRepository
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotosUseCase @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val photoRepository: PhotoRepository,
) {

    @JvmName("toAbsoluteUrlNull")
    suspend fun String?.toAbsoluteUrl(): String? = this?.toAbsoluteUrl()

    suspend fun String.toAbsoluteUrl(): String {
        val serverUrl = serverUseCase.getServerUrl()
        return this
            .removeSuffix(".webp")
            .removeSuffix(".mp4")
            .let { serverUrl + it }
    }

    @JvmName("toThumbnailUrlFromIdNull")
    suspend fun String?.toThumbnailUrlFromId(): String? =
        this?.toThumbnailUrlFromId()

    suspend fun String.toThumbnailUrlFromId(): String =
        "/media/square_thumbnails/$this".toAbsoluteUrl()

    suspend fun String.toFullSizeUrlFromId() = "/media/photos/$this".toAbsoluteUrl()

    fun getPhoto(id: String): Flow<PhotoDetails> =
        photoRepository.getPhoto(id)

}
