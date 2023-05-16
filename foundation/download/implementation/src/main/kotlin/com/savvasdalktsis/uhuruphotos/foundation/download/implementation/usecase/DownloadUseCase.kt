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
package com.savvasdalktsis.uhuruphotos.foundation.download.implementation.usecase

import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.app.DownloadManager.STATUS_FAILED
import android.net.Uri
import android.os.Environment.DIRECTORY_DCIM
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationHeadersUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asyncReturn
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId.Remote
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalktsis.uhuruphotos.foundation.download.api.usecase.DownloadUseCase
import com.savvasdalktsis.uhuruphotos.foundation.download.implementation.repository.DownloadId
import com.savvasdalktsis.uhuruphotos.foundation.download.implementation.repository.DownloadingRepository
import com.savvasdalktsis.uhuruphotos.foundation.download.implementation.repository.MediaId
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

internal class DownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager,
    private val downloadingRepository: DownloadingRepository,
    private val mediaUseCase: MediaUseCase,
    private val authenticationHeadersUseCase: AuthenticationHeadersUseCase,
): DownloadUseCase {

    override suspend fun scheduleMediaDownload(ids: Collection<Remote>) {
        log { "Queuing download of media items [$ids]" }
        if (ids.isEmpty()) {
            return
        }
        for (id in ids) {
            downloadingRepository.setDownloading(MediaId(id.value), DownloadId(-1))
        }
        for (id in ids) {
            queueDownload(id).onFailure {
                downloadingRepository.removeDownloading(MediaId(id.value))
            }
        }
    }

    private suspend fun queueDownload(id: Remote): Result<Unit> =
        mediaUseCase.refreshDetailsNowIfMissing(id).mapCatching {
            val remotePath = mediaUseCase.getMediaItemDetails(id)?.remotePath
            val fullFileName = remotePath?.substringAfterLast("/")

            val url = id.fullResUri
            val isVideo = id.isVideo
            val fileName = fullFileName?.substringBeforeLast(".") ?: id.value
            val extension = fullFileName?.substringAfterLast(".").orEmpty()
            val request = DownloadManager.Request(Uri.parse(url))
                .setAllowedNetworkTypes(NETWORK_WIFI or NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setTitle(fileName)
                .setMimeType(isVideo.mimeType)
                .setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    DIRECTORY_DCIM,
                    fileName.path(isVideo, extension)
                )

            val authenticatedRequest = authenticationHeadersUseCase.headers(url)
                .fold(request) { r, (key, value) ->
                    r.addRequestHeader(key, value)
                }
            val downloadId = downloadManager.enqueue(authenticatedRequest)
            downloadingRepository.setDownloading(MediaId(id.value), DownloadId(downloadId))
        }

    override suspend fun getDownloading(): Set<String> = asyncReturn {
        downloadingRepository.getAll().executeAsList().toSet()
    }

    override fun observeDownloading(): Flow<Set<String>> = downloadingRepository.getAll()
        .asFlow().mapToList().distinctUntilChanged().map { it.toSet() }

    override suspend fun clearFailures() {
        downloadManager.query(Query().setFilterByStatus(STATUS_FAILED)).use { cursor ->
            while (cursor.moveToNext()) {
                val index = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                if (index >= 0) {
                    val downloadId = cursor.getLong(index)
                    downloadingRepository.removeDownloading(DownloadId(downloadId))
                }
            }
        }
    }

    private val Boolean.mimeType get() = if (this) "video/*" else "image/*"
    private fun String.path(isVideo: Boolean, extension: String) =
        "${File.separator}$this." + (extension.ifEmpty { if (isVideo) "mp4" else "jpg" })
}