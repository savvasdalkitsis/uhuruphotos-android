package com.savvasdalkitsis.librephotos.photos.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.librephotos.photos.service.PhotosService
import com.savvasdalkitsis.librephotos.photos.service.model.toPhotoDetails
import com.savvasdalkitsis.librephotos.photos.repository.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PhotoDetailsRetrieveWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val photosService: PhotosService,
    private val photoRepository: PhotoRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            photosService.getPhoto(params.inputData.getString(KEY_ID)!!).toPhotoDetails().apply {
                photoRepository.insertPhoto(this)
            }
            Result.success()
        } catch (e: Exception) {
            if (params.runAttemptCount < 2) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_ID = "id"
        fun workName(id: String) = "setPhotoDetailsRetrieve/$id"
    }
}