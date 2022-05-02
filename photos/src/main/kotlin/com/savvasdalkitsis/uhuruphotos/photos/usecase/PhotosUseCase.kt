package com.savvasdalkitsis.uhuruphotos.photos.usecase

import com.savvasdalkitsis.uhuruphotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.photos.repository.PhotoRepository
import com.savvasdalkitsis.uhuruphotos.photos.worker.PhotoWorkScheduler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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

    suspend fun String.toFullSizeUrlFromId(
        isVideo: Boolean = false,
    ) = when {
        isVideo -> "/media/video/$this".toAbsoluteUrl()
        else -> "/media/photos/$this".toAbsoluteUrl()
    }

    fun getAllPhotos(): Flow<List<PhotoDetails>> =
        photoRepository.getAllPhotos()

    fun getPhoto(id: String): Flow<PhotoDetails> =
        photoRepository.getPhoto(id)

    suspend fun setPhotoFavourite(id: String, favourite: Boolean) {
        photoRepository.setPhotoRating(id, if (favourite) FAVOURITES_RATING_THRESHOLD else 0)
        photoWorkScheduler.schedulePhotoFavourite(id, favourite)
    }

    fun refreshDetails(id: String) {
        photoRepository.refreshDetails(id)
    }

    suspend fun refreshDetailsNowIfMissing(id: String) {
        photoRepository.refreshDetailsNowIfMissing(id)
    }

    fun deletePhoto(id: String) {
        photoWorkScheduler.schedulePhotoDeletion(id)
    }

    companion object {
        const val FAVOURITES_RATING_THRESHOLD = 4
    }
}
