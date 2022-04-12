package com.savvasdalkitsis.librephotos.photos.worker

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import javax.inject.Inject

class PhotoWorkScheduler @Inject constructor(
    private val workManager: WorkManager,
) {

    fun schedulePhotoFavourite(id: String, favourite: Boolean) {
        workManager.enqueueUniqueWork(
            PhotoFavouriteWorker.workName(id),
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<PhotoFavouriteWorker>()
                .setInputData(Data.Builder()
                    .putString(PhotoFavouriteWorker.KEY_ID, id)
                    .putBoolean(PhotoFavouriteWorker.KEY_FAVOURITE, favourite)
                    .build())
                .build(),
        )
    }
}