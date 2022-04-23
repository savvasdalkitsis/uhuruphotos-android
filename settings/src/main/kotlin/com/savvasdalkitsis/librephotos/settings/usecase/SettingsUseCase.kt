package com.savvasdalkitsis.librephotos.settings.usecase

import androidx.work.NetworkType
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    flowSharedPreferences: FlowSharedPreferences,
){

    private val diskCacheSize = flowSharedPreferences.getInt("diskCacheSize", 500)
    private val memCacheSize = flowSharedPreferences.getInt("memCacheSize", 200)
    private val feedSyncFrequency = flowSharedPreferences.getInt("feedSyncFrequency", 12)
    private val shouldPerformPeriodicFeedSync = flowSharedPreferences.getBoolean("shouldPerformPeriodicFeedSync", true)
    private val fullSyncNetworkRequirements = flowSharedPreferences.getEnum("fullSyncNetworkRequirements", NetworkType.NOT_ROAMING)
    private val fullSyncRequiresCharging = flowSharedPreferences.getBoolean("fullSyncRequiresCharging", false)

    fun getDiskCacheMaxLimit(): Int = diskCacheSize.get()
    fun getMemCacheMaxLimit(): Int = memCacheSize.get()
    fun getFeedSyncFrequency(): Int = feedSyncFrequency.get()
    fun getFullSyncNetworkRequirements(): NetworkType = fullSyncNetworkRequirements.get()
    fun getFullSyncRequiresCharging(): Boolean = fullSyncRequiresCharging.get()
    fun getShouldPerformPeriodicFullSync(): Boolean = shouldPerformPeriodicFeedSync.get()

    fun observeDiskCacheMaxLimit(): Flow<Int> = diskCacheSize.asFlow()
    fun observeMemCacheMaxLimit(): Flow<Int> = memCacheSize.asFlow()
    fun observeFeedSyncFrequency(): Flow<Int> = feedSyncFrequency.asFlow()
    fun observeFullSyncNetworkRequirements(): Flow<NetworkType> = fullSyncNetworkRequirements.asFlow()
    fun observeFullSyncRequiresCharging(): Flow<Boolean> = fullSyncRequiresCharging.asFlow()

    suspend fun setDiskCacheMaxLimit(sizeInMb: Int) {
        diskCacheSize.setAndCommit(sizeInMb)
    }

    suspend fun setMemCacheMaxLimit(sizeInMb: Int) {
        memCacheSize.setAndCommit(sizeInMb)
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
