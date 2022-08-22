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
package com.savvasdalkitsis.uhuruphotos.implementation.media.remote.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext

@HiltWorker
class RemoteMediaItemOriginalFileRetrieveWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val imageLoader: ImageLoader,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val videoCache: CacheDataSource.Factory,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(RemoteMediaDownloadDispatcher) {
        try {
            val id = params.inputData.getString(KEY_ID)!!
            val video = params.inputData.getBoolean(KEY_VIDEO, false)
            val url = with(remoteMediaUseCase) {
                id.toFullSizeUrlFromId(video)
            }
            if (video) {
                cacheVideo(url)
            } else {
                cachePhoto(url)
            }
        } catch (e: Exception) {
            log(e)
            if (params.runAttemptCount < 4) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun cacheVideo(url: String): Result {
        CacheWriter(
            videoCache.createDataSourceForDownloading(),
            DataSpec(Uri.parse(url)),
            ByteArray(8 * 1024 * 1024)
        ) { requestLength, bytesCached, _ ->
            setForegroundAsync(
                createForegroundInfo(((bytesCached / requestLength.toFloat()) * 100).toInt())
            )
        }.cache()
        setForegroundAsync(createForegroundInfo(null))
        return Result.success()
    }

    private suspend fun cachePhoto(url: String): Result {
        val result = imageLoader.execute(
            ImageRequest.Builder(context)
                .data(url)
                .build()
        )

        return if (result is SuccessResult) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo() = createForegroundInfo(null)

    private fun createForegroundInfo(progress: Int?) = foregroundInfoBuilder.build(
        applicationContext,
        string.downloading_original_file,
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID,
        progress
    )

    companion object {
        const val KEY_ID = "id"
        const val KEY_VIDEO = "video"
        // string value needs to remain as is for backwards compatibility
        fun workName(id: String) = "photoOriginalFileRetrieve/$id"
        private const val NOTIFICATION_ID = 1279
    }
}