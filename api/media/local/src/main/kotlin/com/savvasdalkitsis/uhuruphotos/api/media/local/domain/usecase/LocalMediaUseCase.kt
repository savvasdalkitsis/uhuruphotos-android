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
package com.savvasdalkitsis.uhuruphotos.api.media.local.domain.usecase

import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalPermissions
import kotlinx.coroutines.flow.Flow

interface LocalMediaUseCase {

    fun Long.toContentUri(isVideo: Boolean): String

    suspend fun getLocalMedia(): List<LocalMediaItem>

    suspend fun getLocalMediaItem(id: Long): LocalMediaItem?

    fun observeLocalMediaItems(): Flow<LocalMediaItems>

    fun observeLocalMediaFolder(folderId: Int): Flow<LocalFolder>

    suspend fun refreshLocalMediaItem(id: Long, isVideo: Boolean)

    suspend fun refreshLocalMediaFolder(folderId: Int)

    suspend fun refreshAll(
        onProgressChange: suspend (Int) -> Unit,
    )

    fun observePermissionsState(): Flow<LocalPermissions>
}