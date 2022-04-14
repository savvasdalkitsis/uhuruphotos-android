package com.savvasdalkitsis.librephotos.photos.worker

import androidx.work.BackoffPolicy
import com.savvasdalkitsis.librephotos.worker.WorkScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhotoWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler
) {

    fun schedulePhotoFavourite(id: String, favourite: Boolean) =
        workScheduler.scheduleNow<PhotoFavouriteWorker>(
            workName = PhotoFavouriteWorker.workName(id)
        ) {
            putString(PhotoFavouriteWorker.KEY_ID, id)
            putBoolean(PhotoFavouriteWorker.KEY_FAVOURITE, favourite)
        }

    fun schedulePhotoDetailsRetrieve(id: String) =
        workScheduler.scheduleNow<PhotoDetailsRetrieveWorker>(
            workName = PhotoDetailsRetrieveWorker.workName(id),
            backoffPolicy = BackoffPolicy.LINEAR,
            backoffDelay = 2,
            backoffTimeUnit = TimeUnit.SECONDS,
        ) {
            putString(PhotoDetailsRetrieveWorker.KEY_ID, id)
        }
}