package com.savvasdalkitsis.librephotos.settings.viewmodel

import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.librephotos.albums.api.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.settings.usecase.CacheUseCase
import com.savvasdalkitsis.librephotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.*
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsEffect.ShowMessage
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsMutation.*
import com.savvasdalkitsis.librephotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.librephotos.viewmodel.Handler
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class SettingsHandler @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val albumWorkScheduler: AlbumWorkScheduler,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val cacheUseCase: CacheUseCase,
) : Handler<SettingsState, SettingsEffect, SettingsAction, SettingsMutation> {

    override fun invoke(
        state: SettingsState,
        action: SettingsAction,
        effect: suspend (SettingsEffect) -> Unit
    ): Flow<SettingsMutation> = when (action) {
        LoadSettings -> merge(
            settingsUseCase.observeDiskCacheMaxLimit()
                .map(::DisplayDiskCacheMaxLimit),
            settingsUseCase.observeMemCacheMaxLimit()
                .map(::DisplayMemCacheMaxLimit),
            settingsUseCase.observeFeedSyncFrequency()
                .map(::DisplayFeedSyncFrequency),
            cacheUseCase.observeDiskCacheCurrentUse()
                .map(::DisplayDiskCacheCurrentUse),
            cacheUseCase.observeMemCacheCurrentUse()
                .map(::DisplayMemCacheCurrentUse),
            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeUpdate),
            albumWorkScheduler.observeAlbumRefreshJobStatus()
                .map {
                    when (it) {
                        RUNNING -> DisableFullSyncButton
                        else -> EnableFullSyncButton
                    }
                }
            )
        NavigateBack -> flow {
            effect(SettingsEffect.NavigateBack)
        }
        is ChangeDiskCache -> flow {
            settingsUseCase.setDiskCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearDiskCache -> flow {
            cacheUseCase.clearDiskCache()
        }
        is ChangeMemCache -> flow {
            settingsUseCase.setMemCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearMemCache -> flow {
            cacheUseCase.clearMemCache()
        }
        is ChangingFeedSyncFrequency -> flow {
            settingsUseCase.setFeedSyncFrequency(action.frequency.toInt())
        }
        FinaliseFeedSyncFrequencyChange -> flow {
            albumWorkScheduler.scheduleAlbumsRefreshPeriodic(
                hoursInterval = settingsUseCase.getFeedSyncFrequency(),
                existingPeriodicWorkPolicy = REPLACE
            )
            effect(ShowMessage("Feed sync frequency changed"))
        }
        AskForFullFeedSync -> flowOf(ShowFullFeedSyncDialog)
        DismissFullFeedSyncDialog -> flowOf(HideFullFeedSyncDialog)
        PerformFullFeedSync -> flow {
            albumWorkScheduler.scheduleAlbumsRefreshNow(shallow = false)
        }
    }

}
