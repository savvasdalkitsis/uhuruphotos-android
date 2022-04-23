package com.savvasdalkitsis.librephotos.settings.usecase

import androidx.work.NetworkType
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import kotlinx.coroutines.flow.Flow
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

    fun getImageDiskCacheMaxLimit(): Int = imageDiskCacheSize.get()
    fun getImageMemCacheMaxLimit(): Int = imageMemCacheSize.get()
    fun getVideoDiskCacheMaxLimit(): Int = videoDiskCacheSize.get()
    fun getFeedSyncFrequency(): Int = feedSyncFrequency.get()
    fun getFullSyncNetworkRequirements(): NetworkType = fullSyncNetworkRequirements.get()
    fun getFullSyncRequiresCharging(): Boolean = fullSyncRequiresCharging.get()
    fun getShouldPerformPeriodicFullSync(): Boolean = shouldPerformPeriodicFeedSync.get()

    fun observeImageDiskCacheMaxLimit(): Flow<Int> = imageDiskCacheSize.asFlow()
    fun observeImageMemCacheMaxLimit(): Flow<Int> = imageMemCacheSize.asFlow()
    fun observeVideoDiskCacheMaxLimit(): Flow<Int> = videoDiskCacheSize.asFlow()
    fun observeFeedSyncFrequency(): Flow<Int> = feedSyncFrequency.asFlow()
    fun observeFullSyncNetworkRequirements(): Flow<NetworkType> = fullSyncNetworkRequirements.asFlow()
    fun observeFullSyncRequiresCharging(): Flow<Boolean> = fullSyncRequiresCharging.asFlow()

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
}
