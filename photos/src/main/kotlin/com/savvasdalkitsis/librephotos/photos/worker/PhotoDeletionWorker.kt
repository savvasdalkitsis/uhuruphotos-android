package com.savvasdalkitsis.librephotos.photos.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.savvasdalkitsis.librephotos.photos.service.PhotosService
import com.savvasdalkitsis.librephotos.photos.repository.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

@HiltWorker
class PhotoDeletionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted val params: WorkerParameters,
    private val photosService: PhotosService,
    private val photoRepository: PhotoRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val id = params.inputData.getString(KEY_ID)!!
            val response = photosService.deletePhoto(id)
            val code = response.code()
            if (code in 200..299) {
                photoRepository.deletePhoto(id)
                Result.success()
            } else {
                failOrRetry()
            }
        } catch (e: Exception) {
            failOrRetry()
        }
    }

    private fun failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        Result.failure()
    }

    companion object {
        const val KEY_ID = "id"
        fun workName(id: String) = "deletePhoto/$id"
    }
}