package com.savvasdalkitsis.librephotos.settings.usecase

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsUseCase @Inject constructor(
    flowSharedPreferences: FlowSharedPreferences,
){

    private val diskCacheSize = flowSharedPreferences.getInt("diskCacheSize", 500)
    private val memCacheSize = flowSharedPreferences.getInt("memCacheSize", 200)

    fun getDiskCacheMaxLimit(): Int = diskCacheSize.get()
    fun getMemCacheMaxLimit(): Int = memCacheSize.get()

    fun observeDiskCacheMaxLimit(): Flow<Int> = diskCacheSize.asFlow()
    fun observeMemCacheMaxLimit(): Flow<Int> = memCacheSize.asFlow()

    suspend fun setDiskCacheMaxLimit(sizeInMb: Int) {
        diskCacheSize.setAndCommit(sizeInMb)
    }

    suspend fun setMemCacheMaxLimit(sizeInMb: Int) {
        memCacheSize.setAndCommit(sizeInMb)
    }
}
