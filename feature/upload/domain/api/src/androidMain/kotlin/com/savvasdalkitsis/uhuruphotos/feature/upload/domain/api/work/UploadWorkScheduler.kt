/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work

import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import kotlinx.coroutines.flow.Flow

interface UploadWorkScheduler {

    fun scheduleUpload(
        force: Boolean,
        item: UploadItem,
        networkType: NetworkType,
        requiresCharging: Boolean,
    )

    fun schedulePostUploadProcessing(hash: MediaItemHash, itemId: Long)

    fun monitorUploadJobs(): Flow<List<WorkInfo?>>

    fun mediaItemIdFrom(workInfo: WorkInfo): Long?
}