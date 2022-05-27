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
package com.savvasdalkitsis.uhuruphotos.settings.usecase

import androidx.work.NetworkType
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    flowSharedPreferences: FlowSharedPreferences,
){

    private val imageDiskCacheSize =
        flowSharedPreferences.getInt("imageDiskCacheSize", 500)
    private val imageMemCacheSize =
        flowSharedPreferences.getInt("imageDiskCacheSize", 200)
    private val videoDiskCacheSize =
        flowSharedPreferences.getInt("videoDiskCacheSize", 700)
    private val feedSyncFrequency =
        flowSharedPreferences.getInt("feedSyncFrequency", 12)
    private val shouldPerformPeriodicFeedSync =
        flowSharedPreferences.getBoolean("shouldPerformPeriodicFeedSync", true)
    private val fullSyncNetworkRequirements =
        flowSharedPreferences.getEnum("fullSyncNetworkRequirements", NetworkType.NOT_ROAMING)
    private val fullSyncRequiresCharging =
        flowSharedPreferences.getBoolean("fullSyncRequiresCharging", false)
    private val themeMode =
        flowSharedPreferences.getEnum("themeMode", ThemeMode.default)
    private val searchSuggestionsEnabled =
        flowSharedPreferences.getBoolean("searchSuggestionsEnabled", true)
    private val shareRemoveGpsData =
        flowSharedPreferences.getBoolean("shareRemoveGpsData", false)
    private val showLibrary =
        flowSharedPreferences.getBoolean("showLibrary", true)

    fun getImageDiskCacheMaxLimit(): Int = imageDiskCacheSize.get()
    fun getImageMemCacheMaxLimit(): Int = imageMemCacheSize.get()
    fun getVideoDiskCacheMaxLimit(): Int = videoDiskCacheSize.get()
    fun getFeedSyncFrequency(): Int = feedSyncFrequency.get()
    fun getFullSyncNetworkRequirements(): NetworkType = fullSyncNetworkRequirements.get()
    fun getFullSyncRequiresCharging(): Boolean = fullSyncRequiresCharging.get()
    fun getShouldPerformPeriodicFullSync(): Boolean = shouldPerformPeriodicFeedSync.get()
    fun getShareRemoveGpsData(): Boolean = shareRemoveGpsData.get()
    fun getShowLibrary(): Boolean = showLibrary.get()

    fun observeImageDiskCacheMaxLimit(): Flow<Int> = imageDiskCacheSize.asFlow()
    fun observeImageMemCacheMaxLimit(): Flow<Int> = imageMemCacheSize.asFlow()
    fun observeVideoDiskCacheMaxLimit(): Flow<Int> = videoDiskCacheSize.asFlow()
    fun observeFeedSyncFrequency(): Flow<Int> = feedSyncFrequency.asFlow()
    fun observeFullSyncNetworkRequirements(): Flow<NetworkType> = fullSyncNetworkRequirements.asFlow()
    fun observeFullSyncRequiresCharging(): Flow<Boolean> = fullSyncRequiresCharging.asFlow()
    fun observeThemeMode(): Flow<ThemeMode> = themeMode.asFlow()
    fun observeSearchSuggestionsEnabledMode(): Flow<Boolean> = searchSuggestionsEnabled.asFlow()
    fun observeShareRemoveGpsData(): Flow<Boolean> = shareRemoveGpsData.asFlow()
    fun observeShowLibrary(): Flow<Boolean> = showLibrary.asFlow()
    suspend fun observeThemeModeState(): StateFlow<ThemeMode> = observeThemeMode().stateIn(
        CoroutineScope(Dispatchers.IO)
    )

    suspend fun setImageDiskCacheMaxLimit(sizeInMb: Int) {
        imageDiskCacheSize.setAndCommit(sizeInMb)
    }

    suspend fun setImageMemCacheMaxLimit(sizeInMb: Int) {
        imageMemCacheSize.setAndCommit(sizeInMb)
    }

    suspend fun setVideoDiskCacheMaxLimit(sizeInMb: Int) {
        videoDiskCacheSize.setAndCommit(sizeInMb)
    }

    suspend fun setFeedSyncFrequency(frequency: Int) {
        feedSyncFrequency.setAndCommit(frequency)
    }

    suspend fun setFullSyncNetworkRequirements(networkType: NetworkType) {
        fullSyncNetworkRequirements.setAndCommit(networkType)
    }

    suspend fun setFullSyncRequiresCharging(requiresCharging: Boolean) {
        fullSyncRequiresCharging.setAndCommit(requiresCharging)
    }

    suspend fun setShouldPerformPeriodicFullSync(perform: Boolean) {
        shouldPerformPeriodicFeedSync.setAndCommit(perform)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        themeMode.setAndCommit(mode)
    }

    suspend fun setSearchSuggestionsEnabled(enabled: Boolean) {
        searchSuggestionsEnabled.setAndCommit(enabled)
    }

    suspend fun setShareRemoveGpsData(enabled: Boolean) {
        shareRemoveGpsData.setAndCommit(enabled)
    }

    suspend fun setShowLibrary(show: Boolean) {
        showLibrary.setAndCommit(show)
    }
}
