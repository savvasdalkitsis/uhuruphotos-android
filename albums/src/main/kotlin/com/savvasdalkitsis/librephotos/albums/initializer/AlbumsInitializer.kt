package com.savvasdalkitsis.librephotos.albums.initializer

import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import com.savvasdalkitsis.librephotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
import com.savvasdalkitsis.librephotos.settings.usecase.SettingsUseCase
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