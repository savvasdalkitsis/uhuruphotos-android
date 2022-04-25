package com.savvasdalkitsis.uhuruphotos.albums.initializer

import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import com.savvasdalkitsis.uhuruphotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
import javax.inject.Inject

class AlbumsInitializer @Inject constructor(
    private val albumWorkScheduler: AlbumWorkScheduler,
): ApplicationCreated {

    override fun onAppCreated() {
        albumWorkScheduler.scheduleAlbumsRefreshPeriodic(
            existingPeriodicWorkPolicy = KEEP
        )
    }

}