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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asFlowList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.GetBuckets
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalMediaFolderRepository @Inject constructor(
    private val localMediaService: LocalMediaService,
    private val localMediaItemDetailsQueries: LocalMediaItemDetailsQueries,
    @PlainTextPreferences
    private val preferences: Preferences,
) {

    // string value needs to remain as is for backwards compatibility
    private val keyDefaultFolderId = "defaultBucketId"
    private val keyShouldScanOtherFolders = "keyShouldScanOtherFolders"

    suspend fun getDefaultLocalFolderId(): Int? = preferences.get<Int?>(keyDefaultFolderId, null) ?:
        localMediaService.getDefaultBucketId()?.also(::setDefaultFolderId)

    fun setDefaultFolderId(folderId: Int) {
        preferences.set(keyDefaultFolderId, folderId)
    }

    fun observeFolders(): Flow<Set<LocalMediaFolder>> =
        localMediaItemDetailsQueries.getBuckets()
            .asFlowList()
            .map {
                it.toMediaBuckets()
            }

    suspend fun getFolders(): Set<LocalMediaFolder> =
        localMediaItemDetailsQueries.getBuckets().awaitList().toMediaBuckets()

    fun shouldScanOtherFolders(): Boolean =
        // true by default for users that had the app before this was introduced
        // will be set to false during Welcome
        preferences.get(keyShouldScanOtherFolders, true)

    fun observeScanningOtherFolders(): Flow<Boolean> =
        // true by default for users that had the app before this was introduced
        // will be set to false during Welcome
        preferences.observe(keyShouldScanOtherFolders, true)

    fun setScanningOtherFolders(scanning: Boolean) {
        preferences.set(keyShouldScanOtherFolders, scanning)
    }

    private fun List<GetBuckets>.toMediaBuckets(): Set<LocalMediaFolder> = map {
        LocalMediaFolder(it.bucketId, it.bucketName)
    }.toSet()
}

