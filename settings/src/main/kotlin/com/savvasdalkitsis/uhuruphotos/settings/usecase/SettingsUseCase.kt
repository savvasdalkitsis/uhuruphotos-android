package com.savvasdalkitsis.uhuruphotos.settings.usecase

import androidx.work.NetworkType
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    flowSharedPreferences: FlowSharedPreferences,
){

    private val imageDiskCacheSize = flowSharedPreferences.getInt("imageDiskCacheSize", 500)
    private val imageMemCacheSize = flowSharedPreferences.getInt("imageDiskCacheSize", 200)
    private val videoDiskCacheSize = flowSharedPreferences.getInt("videoDiskCacheSize", 700)
    private val feedSyncFrequency = flowSharedPreferences.getInt("feedSyncFrequency", 12)
    private val shouldPerformPeriodicFeedSync = flowSharedPreferences.getBoolean("shouldPerformPeriodicFeedSync", true)
    private val fullSyncNetworkRequirements = flowSharedPreferences.getEnum("fullSyncNetworkRequirements", NetworkType.NOT_ROAMING)
    private val fullSyncRequiresCharging = flowSharedPreferences.getBoolean("fullSyncRequiresCharging", false)
    private val themeMode = flowSharedPreferences.getEnum("themeMode", ThemeMode.default)

    fun getImageDiskCacheMaxLimit(): Int = imageDiskCacheSize.get()
    fun getImageMemCacheMaxLimit(): Int = imageMemCacheSize.get()
    fun getVideoDiskCacheMaxLimit(): Int = videoDiskCacheSize.get()
    fun getFeedSyncFrequency(): Int = feedSyncFrequency.get()
    fun getFullSyncNetworkRequirements(): NetworkType = fullSyncNetworkRequirements.get()
    fun getFullSyncRequiresCharging(): Boolean = fullSyncRequiresCharging.get()
    fun getShouldPerformPeriodicFullSync(): Boolean = shouldPerformPeriodicFeedSync.get()
    fun getThemeMode(): ThemeMode = themeMode.get()

    fun observeImageDiskCacheMaxLimit(): Flow<Int> = imageDiskCacheSize.asFlow()
    fun observeImageMemCacheMaxLimit(): Flow<Int> = imageMemCacheSize.asFlow()
    fun observeVideoDiskCacheMaxLimit(): Flow<Int> = videoDiskCacheSize.asFlow()
    fun observeFeedSyncFrequency(): Flow<Int> = feedSyncFrequency.asFlow()
    fun observeFullSyncNetworkRequirements(): Flow<NetworkType> = fullSyncNetworkRequirements.asFlow()
    fun observeFullSyncRequiresCharging(): Flow<Boolean> = fullSyncRequiresCharging.asFlow()
    fun observeThemeMode(): Flow<ThemeMode> = themeMode.asFlow()
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
}
