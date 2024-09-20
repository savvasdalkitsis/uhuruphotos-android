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
package com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase

import androidx.work.NetworkType
import kotlinx.coroutines.flow.Flow

const val minCacheSize = 10

interface SettingsUseCase {

    fun getLightboxPhotoDiskCacheMaxLimit(): Int
    fun getLightboxPhotoMemCacheMaxLimit(): Int
    fun getThumbnailDiskCacheMaxLimit(): Int
    fun getThumbnailMemCacheMaxLimit(): Int
    fun getVideoDiskCacheMaxLimit(): Int
    fun getFeedSyncFrequency(): Int
    @Deprecated("")
    fun getFeedDaysToRefresh(): Int
    fun getFullSyncNetworkRequirements(): NetworkType
    fun getFullSyncRequiresCharging(): Boolean
    fun getCloudSyncNetworkRequirements(): NetworkType
    fun getCloudSyncRequiresCharging(): Boolean
    fun getShouldPerformPeriodicFullSync(): Boolean
    fun getShareRemoveGpsData(): Boolean
    fun getLoggingEnabled(): Boolean
    fun getSendDatabaseEnabled(): Boolean
    fun getBiometricsRequiredForAppAccess(): Boolean
    fun getBiometricsRequiredForHiddenPhotosAccess(): Boolean
    fun getBiometricsRequiredForTrashAccess(): Boolean

    fun observeLightboxPhotoDiskCacheMaxLimit(): Flow<Int>
    fun observeLightboxPhotoMemCacheMaxLimit(): Flow<Int>
    fun observeThumbnailDiskCacheMaxLimit(): Flow<Int>
    fun observeThumbnailMemCacheMaxLimit(): Flow<Int>
    fun observeVideoDiskCacheMaxLimit(): Flow<Int>
    fun observeFeedSyncFrequency(): Flow<Int>
    fun observeFeedDaysToRefresh(): Flow<Int>
    fun observeFullSyncNetworkRequirements(): Flow<NetworkType>
    fun observeFullSyncRequiresCharging(): Flow<Boolean>
    fun observeCloudSyncNetworkRequirements(): Flow<NetworkType>
    fun observeCloudSyncRequiresCharging(): Flow<Boolean>
    fun observeShareRemoveGpsData(): Flow<Boolean>
    fun observeLoggingEnabled(): Flow<Boolean>
    fun observeSendDatabaseEnabled(): Flow<Boolean>
    fun observeBiometricsRequiredForAppAccess(): Flow<Boolean>
    fun observeBiometricsRequiredForHiddenPhotosAccess(): Flow<Boolean>
    fun observeBiometricsRequiredForTrashAccess(): Flow<Boolean>

    fun setLightboxPhotoDiskCacheMaxLimit(sizeInMb: Int)
    fun setLightboxPhotoMemCacheMaxLimit(sizeInMb: Int)
    fun setThumbnailDiskCacheMaxLimit(sizeInMb: Int)
    fun setThumbnailMemCacheMaxLimit(sizeInMb: Int)
    fun setVideoDiskCacheMaxLimit(sizeInMb: Int)
    fun setFeedSyncFrequency(frequency: Int)
    fun setFeedFeedDaysToRefresh(days: Int)
    fun setFullSyncNetworkRequirements(networkType: NetworkType)
    fun setFullSyncRequiresCharging(requiresCharging: Boolean)
    fun setCloudSyncNetworkRequirements(networkType: NetworkType)
    fun setCloudSyncRequiresCharging(requiresCharging: Boolean)
    fun setShouldPerformPeriodicFullSync(perform: Boolean)
    fun setShareRemoveGpsData(enabled: Boolean)
    fun setLoggingEnabled(enabled: Boolean)
    fun setSendDatabaseEnabled(enabled: Boolean)
    fun setBiometricsRequiredForAppAccess(required: Boolean)
    fun setBiometricsRequiredForHiddenPhotosAccess(required: Boolean)
    fun setBiometricsRequiredForTrashAccess(required: Boolean)
}