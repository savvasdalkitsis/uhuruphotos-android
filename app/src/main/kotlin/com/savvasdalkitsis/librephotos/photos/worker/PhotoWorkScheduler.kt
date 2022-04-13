package com.savvasdalkitsis.librephotos.photos.worker

import androidx.work.*
import javax.inject.Inject

class PhotoWorkScheduler @Inject constructor(
    private val workManager: WorkManager,
) {

    fun schedulePhotoFavourite(id: String, favourite: Boolean) =
        schedule<PhotoFavouriteWorker>(PhotoFavouriteWorker.workName(id)) {
            putString(PhotoFavouriteWorker.KEY_ID, id)
            putBoolean(PhotoFavouriteWorker.KEY_FAVOURITE, favourite)
        }

    fun schedulePhotoDetailsRetrieve(id: String) =
        schedule<PhotoDetailsRetrieveWorker>(PhotoDetailsRetrieveWorker.workName(id)) {
            putString(PhotoDetailsRetrieveWorker.KEY_ID, id)
        }

    private inline fun <reified W: CoroutineWorker> schedule(
        workName: String,
        params: Data.Builder.() -> Data.Builder,
    ) {
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<W>()
                .setInputData(params(Data.Builder()).build())
                .build(),
        )
    }
}