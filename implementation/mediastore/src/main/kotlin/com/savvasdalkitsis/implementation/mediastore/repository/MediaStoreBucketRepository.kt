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
package com.savvasdalkitsis.implementation.mediastore.repository

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.NullableSerializer
import com.savvasdalkitsis.implementation.mediastore.service.MediaStoreService
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.MediaBucket
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.api.db.mediastore.GetBuckets
import com.savvasdalkitsis.uhuruphotos.api.db.mediastore.MediaStoreQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaStoreBucketRepository @Inject constructor(
    private val mediaStoreService: MediaStoreService,
    private val mediaStoreQueries: MediaStoreQueries,
    flowSharedPreferences: FlowSharedPreferences,
) {

    private val defaultBucketId = flowSharedPreferences.getNullableObject(
        "defaultBucketId",
        object: NullableSerializer<Int> {
            override fun deserialize(serialized: String?): Int? = serialized?.toIntOrNull()
            override fun serialize(value: Int?): String? = value?.toString()
        },
        null
    )

    suspend fun getDefaultBucketId(): Int? = defaultBucketId.get() ?:
        mediaStoreService.getDefaultBucketId()?.also(::setDefaultBucketId)

    fun setDefaultBucketId(bucketId: Int) {
        defaultBucketId.set(bucketId)
    }

    fun observeBuckets(): Flow<Set<MediaBucket>> =
        mediaStoreQueries.getBuckets().asFlow().mapToList().map {
            it.toMediaBuckets()
        }

    suspend fun getBuckets(): Set<MediaBucket> =
        mediaStoreQueries.getBuckets().await().toMediaBuckets()

    private fun List<GetBuckets>.toMediaBuckets(): Set<MediaBucket> = map {
        MediaBucket(it.bucketId, it.bucketName)
    }.toSet()
}

