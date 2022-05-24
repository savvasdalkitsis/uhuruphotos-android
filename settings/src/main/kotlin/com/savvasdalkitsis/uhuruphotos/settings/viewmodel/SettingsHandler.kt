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
package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.uhuruphotos.albums.api.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.log.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.search.api.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.settings.usecase.CacheUseCase
import com.savvasdalkitsis.uhuruphotos.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.AskForFullFeedSync
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeFullSyncChargingRequirements
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeImageDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeImageMemCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeSearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeThemeMode
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeVideoDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearImageDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearImageMemCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearLogFileClicked
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearRecentSearches
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ClearVideoDiskCache
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.DismissFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.FeedSyncFrequencyChanged
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.LoadSettings
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.PerformFullFeedSync
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.SendFeedbackClicked
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFeedSyncFrequency
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFullSyncNetworkRequirements
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFullSyncProgress
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayFullSyncRequiresCharging
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayImageDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayImageMemCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayMemCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplaySearchSuggestionsEnabled
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayShareGpsDataEnabled
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayThemeMode
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayVideoDiskCacheCurrentUse
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.DisplayVideoDiskCacheMaxLimit
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.EnableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.HideFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.ShowFullFeedSyncDialog
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsMutation.UserBadgeUpdate
import com.savvasdalkitsis.uhuruphotos.strings.R
import com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.viewmodel.Handler
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
            settingsUseCase.observeThemeMode()
                .map(::DisplayThemeMode),
            settingsUseCase.observeSearchSuggestionsEnabledMode()
                .map(::DisplaySearchSuggestionsEnabled),
            settingsUseCase.observeShareRemoveGpsData()
                .map(::DisplayShareGpsDataEnabled),
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
