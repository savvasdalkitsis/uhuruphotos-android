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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model.RefreshJobState
import kotlinx.coroutines.flow.Flow

interface LocalMediaUseCase {

    fun Long.toContentUri(isVideo: Boolean): String

    suspend fun getLocalMediaItems(): LocalMediaItems

    fun observeLocalMediaItem(id: Long): Flow<LocalMediaItem>

    suspend fun getLocalMediaItem(id: Long): LocalMediaItem?

    fun observeLocalMediaItems(): Flow<LocalMediaItems>

    fun observeLocalMediaFolder(folderId: Int): Flow<LocalFolder>

    suspend fun refreshLocalMediaItem(id: Long, isVideo: Boolean): SimpleResult

    suspend fun refreshLocalMediaFolder(folderId: Int): SimpleResult

    suspend fun refreshAll(
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    )

    fun observePermissionsState(): Flow<LocalPermissions>

    fun observeLocalMediaSyncJob(): Flow<RefreshJobState?>

    suspend fun savePhoto(bitmap: Bitmap, name: String, originalFileUri: Uri? = null): Boolean
    suspend fun getDefaultFolderId(): Int?

    fun clearAll()

    fun markLocalMediaSyncedBefore(synced: Boolean)
    fun hasLocalMediaBeenSyncedBefore(): Boolean
}