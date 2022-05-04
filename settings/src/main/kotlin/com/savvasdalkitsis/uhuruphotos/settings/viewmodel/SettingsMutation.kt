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

import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

sealed class SettingsMutation {
    object ShowFullFeedSyncDialog : SettingsMutation()
    object HideFullFeedSyncDialog : SettingsMutation()
    object EnableFullSyncButton : SettingsMutation()
    object DisableFullSyncButton : SettingsMutation()

    data class DisplayFullSyncProgress(val progress: Int) : SettingsMutation()
    data class DisplayDiskCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayMemCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayVideoDiskCacheMaxLimit(val limit: Int) : SettingsMutation()
    data class DisplayImageDiskCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayImageMemCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayVideoDiskCacheCurrentUse(val current: Int): SettingsMutation()
    data class DisplayFeedSyncFrequency(val frequency: Int): SettingsMutation()
    data class DisplayFullSyncNetworkRequirements(val networkType: NetworkType): SettingsMutation()
    data class DisplayFullSyncRequiresCharging(val requiresCharging: Boolean): SettingsMutation()
    data class DisplayThemeMode(val themeMode: ThemeMode): SettingsMutation()
    data class DisplaySearchSuggestionsEnabled(val enabled: Boolean): SettingsMutation()
    data class DisplayShareGpsDataEnabled(val enabled: Boolean): SettingsMutation()
    data class UserBadgeUpdate(val userInformationState: UserInformationState): SettingsMutation()
}
