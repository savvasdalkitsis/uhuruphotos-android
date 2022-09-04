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

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.NullableSerializer
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.await
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.GetBuckets
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalMediaFolderRepository @Inject constructor(
    private val localMediaService: LocalMediaService,
    private val localMediaItemDetailsQueries: LocalMediaItemDetailsQueries,
    flowSharedPreferences: FlowSharedPreferences,
) {

    private val defaultFolderId = flowSharedPreferences.getNullableObject(
        // string value needs to remain as is for backwards compatibility
        "defaultBucketId",
        object: NullableSerializer<Int> {
            override fun deserialize(serialized: String?): Int? = serialized?.toIntOrNull()
            override fun serialize(value: Int?): String? = value?.toString()
        },
        null
    )

    suspend fun getDefaultLocalFolderId(): Int? = defaultFolderId.get() ?:
        localMediaService.getDefaultBucketId()?.also(::setDefaultFolderId)

    fun setDefaultFolderId(folderId: Int) {
        defaultFolderId.set(folderId)
    }

    fun observeFolders(): Flow<Set<LocalMediaFolder>> =
        localMediaItemDetailsQueries.getBuckets().asFlow().mapToList().map {
            it.toMediaBuckets()
        }

    suspend fun getFolders(): Set<LocalMediaFolder> =
        localMediaItemDetailsQueries.getBuckets().await().toMediaBuckets()

    private fun List<GetBuckets>.toMediaBuckets(): Set<LocalMediaFolder> = map {
        LocalMediaFolder(it.bucketId, it.bucketName)
    }.toSet()
}

