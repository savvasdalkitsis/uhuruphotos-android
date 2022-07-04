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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.usecase

import android.content.Context
import androidx.work.NetworkType
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.google.android.gms.common.ConnectionResult.*
import com.google.android.gms.common.GoogleApiAvailability
import com.savvasdalkitsis.uhuruphotos.api.log.Log
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapProvider
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapProvider.Google
import com.savvasdalkitsis.uhuruphotos.api.map.model.MapProvider.MapBox
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

internal class SettingsUseCase @Inject constructor(
    flowSharedPreferences: FlowSharedPreferences,
    @ApplicationContext private val context: Context,
) : SettingsUseCase {

    private val imageDiskCacheSize =
        flowSharedPreferences.getInt("imageDiskCacheSize", 500)
    private val imageMemCacheSize =
        flowSharedPreferences.getInt("imageDiskCacheSize", 200)
    private val videoDiskCacheSize =
        flowSharedPreferences.getInt("videoDiskCacheSize", 700)
    private val feedSyncFrequency =
        flowSharedPreferences.getInt("feedSyncFrequency", 12)
    private val feedDaysToRefresh =
        flowSharedPreferences.getInt("feedDaysToRefresh", 3)
    private val shouldPerformPeriodicFeedSync =
        flowSharedPreferences.getBoolean("shouldPerformPeriodicFeedSync", true)
    private val fullSyncNetworkRequirements =
        flowSharedPreferences.getEnum("fullSyncNetworkRequirements", NetworkType.NOT_ROAMING)
    private val fullSyncRequiresCharging =
        flowSharedPreferences.getBoolean("fullSyncRequiresCharging", false)
    private val themeMode =
        flowSharedPreferences.getEnum("themeMode", ThemeMode.default)
    private val mapProvider =
        flowSharedPreferences.getEnum("mapProvider", MapProvider.default)
    private val searchSuggestionsEnabled =
        flowSharedPreferences.getBoolean("searchSuggestionsEnabled", true)
    private val shareRemoveGpsData =
        flowSharedPreferences.getBoolean("shareRemoveGpsData", false)
    private val showLibrary =
        flowSharedPreferences.getBoolean("showLibrary", true)
    private val loggingEnabled =
        flowSharedPreferences.getBoolean("loggingEnabled", false)
    private val biometricsRequiredForAppAccess =
        flowSharedPreferences.getBoolean("biometricsRequiredForAppAccess", false)
    private val biometricsRequiredForHiddenPhotosAccess =
        flowSharedPreferences.getBoolean("biometricsRequiredForHiddenPhotosAccess", false)

    override fun getImageDiskCacheMaxLimit(): Int = imageDiskCacheSize.get()
    override fun getImageMemCacheMaxLimit(): Int = imageMemCacheSize.get()
    override fun getVideoDiskCacheMaxLimit(): Int = videoDiskCacheSize.get()
    override fun getFeedSyncFrequency(): Int = feedSyncFrequency.get()
    override fun getFeedDaysToRefresh(): Int = feedDaysToRefresh.get()

    override fun getFullSyncNetworkRequirements(): NetworkType = fullSyncNetworkRequirements.get()
    override fun getFullSyncRequiresCharging(): Boolean = fullSyncRequiresCharging.get()
    override fun getShouldPerformPeriodicFullSync(): Boolean = shouldPerformPeriodicFeedSync.get()
    override fun getShareRemoveGpsData(): Boolean = shareRemoveGpsData.get()
    override fun getShowLibrary(): Boolean = showLibrary.get()
    override fun getMapProvider(): MapProvider = mapProvider.get().mapToAvailable()
    override fun getAvailableMapProviders(): Set<MapProvider> = MapProvider.values()
        .map { it.mapToAvailable() }.toSet()
    override fun getLoggingEnabled(): Boolean = loggingEnabled.get()
    override fun getBiometricsRequiredForAppAccess(): Boolean = biometricsRequiredForAppAccess.get()
    override fun getBiometricsRequiredForHiddenPhotosAccess(): Boolean =
        biometricsRequiredForHiddenPhotosAccess.get()

    override fun observeImageDiskCacheMaxLimit(): Flow<Int> = imageDiskCacheSize.asFlow()
    override fun observeImageMemCacheMaxLimit(): Flow<Int> = imageMemCacheSize.asFlow()
    override fun observeVideoDiskCacheMaxLimit(): Flow<Int> = videoDiskCacheSize.asFlow()
    override fun observeFeedSyncFrequency(): Flow<Int> = feedSyncFrequency.asFlow()
    override fun observeFeedDaysToRefresh(): Flow<Int> = feedDaysToRefresh.asFlow()

    override fun observeFullSyncNetworkRequirements(): Flow<NetworkType> = fullSyncNetworkRequirements.asFlow()
    override fun observeFullSyncRequiresCharging(): Flow<Boolean> = fullSyncRequiresCharging.asFlow()
    override fun observeThemeMode(): Flow<ThemeMode> = themeMode.asFlow()
    override fun observeSearchSuggestionsEnabledMode(): Flow<Boolean> = searchSuggestionsEnabled.asFlow()
    override fun observeShareRemoveGpsData(): Flow<Boolean> = shareRemoveGpsData.asFlow()
    override fun observeShowLibrary(): Flow<Boolean> = showLibrary.asFlow()
    override suspend fun observeThemeModeState(): StateFlow<ThemeMode> = observeThemeMode().stateIn(
        CoroutineScope(Dispatchers.IO)
    )
    override fun observeMapProvider(): Flow<MapProvider> = mapProvider.asFlow()
        .map { it.mapToAvailable() }
    override fun observeLoggingEnabled(): Flow<Boolean> = loggingEnabled.asFlow()
    override fun observeBiometricsRequiredForAppAccess(): Flow<Boolean> =
        biometricsRequiredForAppAccess.asFlow()
    override fun observeBiometricsRequiredForHiddenPhotosAccess(): Flow<Boolean> =
        biometricsRequiredForHiddenPhotosAccess.asFlow()

    override suspend fun setImageDiskCacheMaxLimit(sizeInMb: Int) {
        imageDiskCacheSize.setAndCommit(sizeInMb)
    }

    override suspend fun setImageMemCacheMaxLimit(sizeInMb: Int) {
        imageMemCacheSize.setAndCommit(sizeInMb)
    }

    override suspend fun setVideoDiskCacheMaxLimit(sizeInMb: Int) {
        videoDiskCacheSize.setAndCommit(sizeInMb)
    }

    override suspend fun setFeedSyncFrequency(frequency: Int) {
        feedSyncFrequency.setAndCommit(frequency)
    }

    override suspend fun setFeedFeedDaysToRefresh(days: Int) {
        feedDaysToRefresh.setAndCommit(days)
    }

    override suspend fun setFullSyncNetworkRequirements(networkType: NetworkType) {
        fullSyncNetworkRequirements.setAndCommit(networkType)
    }

    override suspend fun setFullSyncRequiresCharging(requiresCharging: Boolean) {
        fullSyncRequiresCharging.setAndCommit(requiresCharging)
    }

    override suspend fun setShouldPerformPeriodicFullSync(perform: Boolean) {
        shouldPerformPeriodicFeedSync.setAndCommit(perform)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        themeMode.setAndCommit(mode)
    }

    override suspend fun setSearchSuggestionsEnabled(enabled: Boolean) {
        searchSuggestionsEnabled.setAndCommit(enabled)
    }

    override suspend fun setShareRemoveGpsData(enabled: Boolean) {
        shareRemoveGpsData.setAndCommit(enabled)
    }

    override suspend fun setShowLibrary(show: Boolean) {
        showLibrary.setAndCommit(show)
    }

    override suspend fun setMapProvider(provider: MapProvider) {
        mapProvider.setAndCommit(provider.mapToAvailable())
    }

    override suspend fun setLoggingEnabled(enabled: Boolean) {
        loggingEnabled.setAndCommit(enabled)
        Log.enabled = enabled
    }

    override suspend fun setBiometricsRequiredForAppAccess(required: Boolean) {
        biometricsRequiredForAppAccess.setAndCommit(required)
    }

    override suspend fun setBiometricsRequiredForHiddenPhotosAccess(required: Boolean) {
        biometricsRequiredForHiddenPhotosAccess.setAndCommit(required)
    }

    private fun MapProvider.mapToAvailable(): MapProvider =
        when {
            this == Google && !googlePlayAvailable() -> MapBox
            else -> this
        }

    private fun googlePlayAvailable() =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) in setOf(
            SUCCESS, SERVICE_VERSION_UPDATE_REQUIRED, SIGN_IN_REQUIRED, SERVICE_UPDATING
        )

}
