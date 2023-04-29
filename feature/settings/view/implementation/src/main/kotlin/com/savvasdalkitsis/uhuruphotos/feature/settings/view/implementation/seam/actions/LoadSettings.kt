/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisablePrecacheThumbnailsButton
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayBiometrics
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayFullSyncProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayMapProviders
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayNoMapProvidersOptions
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.DisplayPrecacheThumbnailsProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.EnableFullSyncButton
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.EnablePrecacheThumbnailsButton
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.HideFullSyncProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation.HidePrecacheThumbnailsProgress
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting.Enrolled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting.NotEnrolled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

object LoadSettings : SettingsAction() {
    context(SettingsActionsContext) override fun handle(
        state: SettingsState,
        effect: EffectHandler<SettingsEffect>
    ) = merge(
        settingsUseCase.observeImageDiskCacheMaxLimit()
            .map(SettingsMutation::DisplayDiskCacheMaxLimit),
        settingsUseCase.observeImageMemCacheMaxLimit()
            .map(SettingsMutation::DisplayMemCacheMaxLimit),
        settingsUseCase.observeVideoDiskCacheMaxLimit()
            .map(SettingsMutation::DisplayVideoDiskCacheMaxLimit),
        settingsUseCase.observeFeedSyncFrequency()
            .map(SettingsMutation::DisplayFeedSyncFrequency),
        settingsUseCase.observeFeedDaysToRefresh()
            .map(SettingsMutation::DisplayFeedDaystoRefresh),
        settingsUseCase.observeFullSyncNetworkRequirements()
            .map(SettingsMutation::DisplayFullSyncNetworkRequirements),
        settingsUseCase.observeFullSyncRequiresCharging()
            .map(SettingsMutation::DisplayFullSyncRequiresCharging),
        settingsUseCase.observeThemeMode()
            .map(SettingsMutation::DisplayThemeMode),
        settingsUseCase.observeSearchSuggestionsEnabledMode()
            .map(SettingsMutation::DisplaySearchSuggestionsEnabled),
        settingsUseCase.observeShareRemoveGpsData()
            .map(SettingsMutation::DisplayShareGpsDataEnabled),
        settingsUseCase.observeShowLibrary()
            .map(SettingsMutation::DisplayShowLibrary),
        settingsUseCase.observeMemoriesEnabled()
            .map(SettingsMutation::DisplayShowMemories),
        settingsUseCase.observeAnimateVideoThumbnails()
            .map(SettingsMutation::DisplayAnimateVideoThumbnailsEnabled),
        settingsUseCase.observeMaxAnimatedVideoThumbnails()
            .map(SettingsMutation::DisplayMaxAnimatedVideoThumbnails),
        settingsUseCase.observeMapProvider().map { current ->
            val available = settingsUseCase.getAvailableMapProviders()
            when {
                available.size > 1 -> DisplayMapProviders(current, available)
                else -> DisplayNoMapProvidersOptions
            }
        },
        settingsUseCase.observeLoggingEnabled()
            .map(SettingsMutation::DisplayLoggingEnabled),
        combine(
            settingsUseCase.observeBiometricsRequiredForAppAccess(),
            settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess(),
            settingsUseCase.observeBiometricsRequiredForTrashAccess(),
        ) { app, hiddenPhotos, trash ->
            DisplayBiometrics(enrollment(app, hiddenPhotos, trash))
        },
        cacheUseCase.observeImageDiskCacheCurrentUse()
            .map(SettingsMutation::DisplayImageDiskCacheCurrentUse),
        cacheUseCase.observeImageMemCacheCurrentUse()
            .map(SettingsMutation::DisplayImageMemCacheCurrentUse),
        cacheUseCase.observeVideoDiskCacheCurrentUse()
            .map(SettingsMutation::DisplayVideoDiskCacheCurrentUse),
        avatarUseCase.getAvatarState()
            .map(SettingsMutation::AvatarUpdate),
        flowOf(systemUseCase.getAvailableSystemMemoryInMb())
            .map(SettingsMutation::SetMemoryCacheUpperLimit),
        flowOf(systemUseCase.getAvailableStorageInMb())
            .map(SettingsMutation::SetDiskCacheUpperLimit),
        combine(
            feedWorkScheduler.observeFeedRefreshJob(),
            feedWorkScheduler.observePrecacheThumbnailsJob(),
        ) { feedRefresh, precacheThumbnails ->
            feedRefresh to precacheThumbnails
        }.flatMapMerge { (feedRefresh, precacheThumbnails) ->
            flow {
                when  {
                    feedRefresh?.status == WorkInfo.State.RUNNING -> {
                        emit(DisableFullSyncButton)
                        emit(DisablePrecacheThumbnailsButton)
                        emit(DisplayFullSyncProgress(feedRefresh.progress))
                        emit(HidePrecacheThumbnailsProgress)
                    }
                    precacheThumbnails?.status == WorkInfo.State.RUNNING -> {
                        emit(DisableFullSyncButton)
                        emit(DisablePrecacheThumbnailsButton)
                        emit(DisplayPrecacheThumbnailsProgress(precacheThumbnails.progress))
                        emit(HideFullSyncProgress)
                    }
                    else -> {
                        emit(EnableFullSyncButton)
                        emit(EnablePrecacheThumbnailsButton)
                    }
                }
            }
        }
    )

    context(SettingsActionsContext)
    private fun enrollment(
        app: Boolean,
        hiddenPhotos: Boolean,
        trash: Boolean,
    ) = when (biometricsUseCase.getBiometrics()) {
        Biometrics.Enrolled -> Enrolled(app, hiddenPhotos, trash)
        Biometrics.NotEnrolled -> NotEnrolled
        Biometrics.NoHardware -> null
    }
}
