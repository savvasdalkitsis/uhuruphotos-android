package com.savvasdalkitsis.librephotos.albums.initializer

import com.savvasdalkitsis.librephotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
import javax.inject.Inject

class AlbumsInitializer @Inject constructor(
    private val albumWorkScheduler: AlbumWorkScheduler,
): ApplicationCreated {

    override fun onAppCreated() {
        albumWorkScheduler.scheduleAlbumsRefreshPeriodic()
    }
}