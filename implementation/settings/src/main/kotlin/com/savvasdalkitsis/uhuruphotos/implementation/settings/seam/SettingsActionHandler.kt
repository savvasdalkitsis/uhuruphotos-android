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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.seam

import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.uhuruphotos.api.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.biometrics.model.Biometrics.*
import com.savvasdalkitsis.uhuruphotos.api.biometrics.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.api.log.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.search.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.*
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsMutation.*
import com.savvasdalkitsis.uhuruphotos.implementation.settings.usecase.CacheUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.BiometricsSetting
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class SettingsActionHandler @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val albumWorkScheduler: AlbumWorkScheduler,
    private val userBadgeUseCase: UserBadgeUseCase,
    private val cacheUseCase: CacheUseCase,
    private val feedbackUseCase: FeedbackUseCase,
    private val searchUseCase: SearchUseCase,
    private val biometricsUseCase: BiometricsUseCase,
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
            settingsUseCase.observeFeedDaysToRefresh()
                .map(::DisplayFeedDaystoRefresh),
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
            settingsUseCase.observeMapProvider().map { current ->
                val available = settingsUseCase.getAvailableMapProviders()
                when {
                    available.size > 1 -> DisplayMapProviders(current, available)
                    else -> DisplayNoMapProvidersOptions
                }
            },
            settingsUseCase.observeLoggingEnabled()
                .map(::DisplayLoggingEnabled),
            combine(
                settingsUseCase.observeBiometricsRequiredForAppAccess(),
                settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess(),
            ) { app, hiddenPhotos ->
                DisplayBiometrics(enrollment(app, hiddenPhotos))
            },
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
        is ChangeMapProvider -> flow {
            settingsUseCase.setMapProvider(action.mapProvider)
        }
        is ChangeLoggingEnabled -> flow {
            settingsUseCase.setLoggingEnabled(action.enabled)
        }
        is FeedRefreshChanged -> flow {
            settingsUseCase.setFeedFeedDaysToRefresh(action.days)
        }
        is ChangeBiometricsAppAccessRequirement -> authenticateIfNeeded(action.required) {
            settingsUseCase.setBiometricsRequiredForAppAccess(it)
        }
        is ChangeBiometricsHiddenPhotosAccessRequirement -> authenticateIfNeeded(action.required) {
            settingsUseCase.setBiometricsRequiredForHiddenPhotosAccess(it)
        }
        EnrollToBiometrics -> flow {
            effect(SettingsEffect.EnrollToBiometrics)
        }
    }

    private fun enrollment(app: Boolean, hiddenPhotos: Boolean) = when (biometricsUseCase.getBiometrics()) {
        Enrolled -> BiometricsSetting.Enrolled(app, hiddenPhotos)
        NotEnrolled -> BiometricsSetting.NotEnrolled
        NoHardware -> null
    }

    private fun authenticateIfNeeded(
        required: Boolean,
        change: suspend (Boolean) -> Unit,
    ) = flow<SettingsMutation> {
        val proceed = when {
            required -> Result.success(Unit)
            else -> biometricsUseCase.authenticate(
                R.string.authenticate,
                R.string.authenticate_to_change,
                R.string.authenticate_to_change_description,
                true,
            )
        }
        if (proceed.isSuccess) {
            change(required)
        }
    }

}
