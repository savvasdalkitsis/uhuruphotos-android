package com.savvasdalkitsis.librephotos.photos.usecase

import com.savvasdalkitsis.librephotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.db.photos.PhotoDetails
import com.savvasdalkitsis.librephotos.photos.repository.PhotoRepository
import com.savvasdalkitsis.librephotos.photos.worker.PhotoWorkScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class PhotosUseCase @Inject constructor(
    private val serverUseCase: ServerUseCase,
    private val photoRepository: PhotoRepository,
    private val photoWorkScheduler: PhotoWorkScheduler,
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

    suspend fun setPhotoFavourite(id: String, favourite: Boolean) {
        photoRepository.setPhotoRating(id, if (favourite) FAVOURITES_RATING_THRESHOLD else 0)
        photoWorkScheduler.schedulePhotoFavourite(id, favourite)
    }

    fun refreshDetails(id: String) {
        photoRepository.refreshDetails(id)
    }

    companion object {
        const val FAVOURITES_RATING_THRESHOLD = 4
    }
}
