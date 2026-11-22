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
package com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.usecase

import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.app.DownloadManager.Request.NETWORK_MOBILE
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.app.DownloadManager.STATUS_PAUSED
import android.app.DownloadManager.STATUS_PENDING
import android.app.DownloadManager.STATUS_RUNNING
import android.net.Uri
import android.os.Environment.DIRECTORY_DCIM
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.github.michaelbull.result.onFailure
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationHeadersUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel.RemoteIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.deserializePaths
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.asyncReturn
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.andThenTry
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalktsis.uhuruphotos.feature.download.domain.api.usecase.DownloadUseCase
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.DownloadId
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.DownloadingRepository
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.MediaId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
internal class DownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager,
    private val downloadingRepository: DownloadingRepository,
    private val mediaUseCase: MediaUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val authenticationHeadersUseCase: AuthenticationHeadersUseCase,
    private val serverUseCase: ServerUseCase,
): DownloadUseCase {

    private val queuing = AtomicInteger()

    override suspend fun scheduleMediaDownload(ids: Collection<RemoteIdModel>) {
        log { "Queuing download of media items [$ids]" }
        if (ids.isEmpty()) {
            return
        }
        queuing.incrementAndGet()
        try {
            queueDownloads(ids)
        } finally {
            queuing.decrementAndGet()
        }
    }

    private suspend fun queueDownloads(ids: Collection<RemoteIdModel>) {
        for (id in ids) {
            downloadingRepository.setDownloading(MediaId(id.value), QUEUING)
        }
        for (id in ids) {
            queueDownload(id).onFailure {
                downloadingRepository.removeDownloading(MediaId(id.value))
            }
        }
    }

    private suspend fun queueDownload(id: RemoteIdModel): SimpleResult =
        mediaUseCase.refreshDetailsNowIfMissing(id).andThenTry {
            val serverUrl = serverUseCase.getServerUrl()!!
            val remotePath = remoteMediaUseCase.observeRemoteMediaItemDetails(id.value)
                .firstOrNull()?.imagePath?.deserializePaths?.firstOrNull()
            val fullFileName = remotePath?.substringAfterLast("/")

            val url = id.fullResUri(serverUrl)
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
        .asFlow().mapToList(Dispatchers.IO).distinctUntilChanged().map { it.toSet() }

    override suspend fun clearFailuresAndStale() {
        val active = getActive() ?: return
        (downloadingRepository.getAllDownloadIds() - active - stillQueuing())
            .forEach {
                downloadingRepository.removeDownloading(DownloadId(it))
            }
    }

    private fun stillQueuing(): Set<Long> = if (queuing.get() > 0) setOf(QUEUING.id) else emptySet()

    private fun getActive(): Set<Long>? =
        downloadManager.query(
            Query().setFilterByStatus(STATUS_RUNNING or STATUS_PAUSED or STATUS_PENDING)
        )?.use { cursor ->
            buildSet {
                while (cursor.moveToNext()) {
                    val index = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                    if (index >= 0) {
                        add(cursor.getLong(index))
                    }
                }
            }
        }

    private val Boolean.mimeType get() = if (this) "video/*" else "image/*"
    private fun String.path(isVideo: Boolean, extension: String) =
        "${File.separator}$this." + (extension.ifEmpty { if (isVideo) "mp4" else "jpg" })

    private companion object {
        val QUEUING = DownloadId(-1)
    }
}
