package com.savvasdalkitsis.librephotos.photos.worker

import com.savvasdalkitsis.librephotos.worker.WorkScheduler
import javax.inject.Inject

class PhotoWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler
) {

    fun schedulePhotoFavourite(id: String, favourite: Boolean) =
        workScheduler.scheduleNow<PhotoFavouriteWorker>(PhotoFavouriteWorker.workName(id)) {
            putString(PhotoFavouriteWorker.KEY_ID, id)
            putBoolean(PhotoFavouriteWorker.KEY_FAVOURITE, favourite)
        }

    fun schedulePhotoDetailsRetrieve(id: String) =
        workScheduler.scheduleNow<PhotoDetailsRetrieveWorker>(PhotoDetailsRetrieveWorker.workName(id)) {
            putString(PhotoDetailsRetrieveWorker.KEY_ID, id)
        }
}