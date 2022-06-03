/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.settings.seam

import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.api.log.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.albums.api.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.search.api.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.AskForFullFeedSync
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeFullSyncChargingRequirements
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeImageDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeImageMemCache
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeSearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeShowLibrary
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeThemeMode
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ChangeVideoDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ClearImageDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ClearImageMemCache
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ClearLogFileClicked
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ClearRecentSearches
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.ClearVideoDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.FeedSyncFrequencyChanged
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.LoadSettings
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.PerformFullFeedSync
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsAction.SendFeedbackClicked
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayFeedSyncFrequency
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayFullSyncProgress
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayFullSyncRequiresCharging
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayImageDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayImageMemCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayMemCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplaySearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayShowLibrary
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayThemeMode
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayVideoDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.DisplayVideoDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.EnableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.HideFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.ShowFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsMutation.UserBadgeUpdate
import com.savvasdalkitsis.uhuruphotos.settings.usecase.CacheUseCase
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

internal class SettingsHandler @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val albumWorkScheduler: AlbumWorkScheduler,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val cacheUseCase: CacheUseCase,
    private val feedbackUseCase: FeedbackUseCase,
    private val searchUseCase: SearchUseCase,
) : ActionHandler<SettingsState, SettingsEffect, SettingsAction, SettingsMutation> {

    override fun handleAction(
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
            settingsUseCase.observeThemeMode()
                .map(::DisplayThemeMode),
            settingsUseCase.observeSearchSuggestionsEnabledMode()
                .map(::DisplaySearchSuggestionsEnabled),
            settingsUseCase.observeShareRemoveGpsData()
                .map(::DisplayShareGpsDataEnabled),
            settingsUseCase.observeShowLibrary()
                .map(::DisplayShowLibrary),
            cacheUseCase.observeImageDiskCacheCurrentUse()
                .map(::DisplayImageDiskCacheCurrentUse),
            cacheUseCase.observeImageMemCacheCurrentUse()
                .map(::DisplayImageMemCacheCurrentUse),
            cacheUseCase.observeVideoDiskCacheCurrentUse()
                .map(::DisplayVideoDiskCacheCurrentUse),
            userBadgeUseCase.getUserBadgeState()
                .map(::UserBadgeUpdate),
            albumWorkScheduler.observeAlbumRefreshJob()
                .flatMapMerge {
                    flowOf(
                        when (it.status) {
                            RUNNING -> DisableFullSyncButton
                            else -> EnableFullSyncButton
                        },
                        DisplayFullSyncProgress(it.progress)
                    )
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
            effect(ShowMessage(R.string.feed_sync_freq_changed))
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
            effect(ShowMessage(R.string.feed_sync_network_changed))
        }
        is ChangeFullSyncChargingRequirements -> flow {
            settingsUseCase.setFullSyncRequiresCharging(action.requiredCharging)
            albumWorkScheduler.scheduleAlbumsRefreshPeriodic(REPLACE)
            effect(ShowMessage(R.string.feed_sync_charging_changed))
        }
        is ChangeThemeMode -> flow {
            settingsUseCase.setThemeMode(action.themeMode)
        }
        is ChangeSearchSuggestionsEnabled -> flow {
            settingsUseCase.setSearchSuggestionsEnabled(action.enabled)
        }
        is ChangeShareGpsDataEnabled -> flow {
            settingsUseCase.setShareRemoveGpsData(action.enabled)
        }
        is ChangeShowLibrary -> flow {
            settingsUseCase.setShowLibrary(action.show)
        }
        ClearLogFileClicked -> flow {
            feedbackUseCase.clearLogs()
            effect(ShowMessage(R.string.logs_cleared))
        }
        SendFeedbackClicked -> flow {
            feedbackUseCase.sendFeedback()
        }
        ClearRecentSearches -> flow {
            searchUseCase.clearRecentSearchSuggestions()
            effect(ShowMessage(R.string.recent_searches_cleared))
        }
    }

}
