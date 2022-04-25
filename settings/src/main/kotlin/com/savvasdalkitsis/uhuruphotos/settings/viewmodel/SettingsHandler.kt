package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.uhuruphotos.albums.api.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.settings.usecase.CacheUseCase
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.*
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.*
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
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
            settingsUseCase.observeImageDiskCacheMaxLimit()
                .map(::DisplayDiskCacheMaxLimit),
            settingsUseCase.observeImageMemCacheMaxLimit()
                .map(::DisplayMemCacheMaxLimit),
            settingsUseCase.observeVideoDiskCacheMaxLimit()
                .map(::DisplayVideoDiskCacheMaxLimit),
            settingsUseCase.observeFeedSyncFrequency()
                .map(::DisplayFeedSyncFrequency),
            settingsUseCase.observeFullSyncNetworkRequirements()
                .map(::DisplayFullSyncNetworkRequirements),
            settingsUseCase.observeFullSyncRequiresCharging()
                .map(::DisplayFullSyncRequiresCharging),
            cacheUseCase.observeImageDiskCacheCurrentUse()
                .map(::DisplayImageDiskCacheCurrentUse),
            cacheUseCase.observeImageMemCacheCurrentUse()
                .map(::DisplayImageMemCacheCurrentUse),
            cacheUseCase.observeVideoDiskCacheCurrentUse()
                .map(::DisplayVideoDiskCacheCurrentUse),
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
        is ChangeImageDiskCache -> flow {
            settingsUseCase.setImageDiskCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearImageDiskCache -> flow {
            cacheUseCase.clearImageDiskCache()
        }
        is ChangeImageMemCache -> flow {
            settingsUseCase.setImageMemCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearImageMemCache -> flow {
            cacheUseCase.clearImageMemCache()
        }
        is ChangeVideoDiskCache -> flow {
            settingsUseCase.setVideoDiskCacheMaxLimit(action.sizeInMb.toInt())
        }
        ClearVideoDiskCache -> flow {
            cacheUseCase.clearVideoDiskCache()
        }
        is FeedSyncFrequencyChanged -> flow {
            settingsUseCase.setFeedSyncFrequency(action.frequency.toInt())
            settingsUseCase.setShouldPerformPeriodicFullSync(action.frequency != action.upperLimit)
            albumWorkScheduler.scheduleAlbumsRefreshPeriodic(REPLACE)
            effect(ShowMessage("Feed sync frequency changed"))
        }
        AskForFullFeedSync -> flowOf(ShowFullFeedSyncDialog)
        DismissFullFeedSyncDialog -> flowOf(HideFullFeedSyncDialog)
        PerformFullFeedSync -> flow {
            albumWorkScheduler.scheduleAlbumsRefreshNow(shallow = false)
            emit(HideFullFeedSyncDialog)
        }
        is ChangeFullSyncNetworkRequirements -> flow {
            settingsUseCase.setFullSyncNetworkRequirements(action.networkType)
            albumWorkScheduler.scheduleAlbumsRefreshPeriodic(REPLACE)
            effect(ShowMessage("Feed sync network requirement changed"))
        }
        is ChangeFullSyncChargingRequirements -> flow {
            settingsUseCase.setFullSyncRequiresCharging(action.requiredCharging)
            albumWorkScheduler.scheduleAlbumsRefreshPeriodic(REPLACE)
            effect(ShowMessage("Feed sync charging requirements changed"))
        }
    }

}
