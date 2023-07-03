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
package com.savvasdalkitsis.uhuruphotos.foundation.download.implementation.repository

import app.cash.sqldelight.Query
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import javax.inject.Inject

internal class DownloadingRepository @Inject constructor(
    private val downloadingMediaItemQueries: DownloadingMediaItemsQueries,
) {

    suspend fun setDownloading(mediaId: MediaId, downloadId: DownloadId) {
        async {
            downloadingMediaItemQueries.insert(DownloadingMediaItems(mediaId.id, downloadId.id))
        }
    }

    suspend fun removeDownloading(id: MediaId) {
        async {
            downloadingMediaItemQueries.removeStartingWith(id.id)
        }
    }

    suspend fun removeDownloading(id: DownloadId) {
        async {
            downloadingMediaItemQueries.removeDownloadId(id.id)
        }
    }

    fun getAll(): Query<String> = downloadingMediaItemQueries.getAll()
    fun getAllDownloadIds(): Set<Long> = downloadingMediaItemQueries.getAllDownloadIds()
        .executeAsList().toSet()
}

@JvmInline
internal value class MediaId(val id: String)
@JvmInline
internal value class DownloadId(val id: Long)