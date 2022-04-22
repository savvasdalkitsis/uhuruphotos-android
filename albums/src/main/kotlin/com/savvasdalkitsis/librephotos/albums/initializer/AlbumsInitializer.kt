package com.savvasdalkitsis.librephotos.albums.initializer

import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import com.savvasdalkitsis.librephotos.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
import com.savvasdalkitsis.librephotos.settings.usecase.SettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlbumsInitializer @Inject constructor(
    private val albumWorkScheduler: AlbumWorkScheduler,
    private val settingsUseCase: SettingsUseCase,
): ApplicationCreated {

    override fun onAppCreated() {
        CoroutineScope(Dispatchers.Default).launch {
            settingsUseCase.observeFeedSyncFrequency().collectIndexed { index, frequency ->
                val policy = when (index) {
                    0 -> KEEP
                    else -> REPLACE
                }
                albumWorkScheduler.scheduleAlbumsRefreshPeriodic(frequency, policy)
            }
        }
    }
}