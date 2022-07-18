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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.worker

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.notification.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.api.notification.NotificationChannels.JOBS_CHANNEL_ID
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class PhotoOriginalFileRetrieveWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val imageLoader: ImageLoader,
    private val photosUseCase: PhotosUseCase,
    private val videoCache: CacheDataSource.Factory,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        try {
            val id = params.inputData.getString(KEY_ID)!!
            val video = params.inputData.getBoolean(KEY_VIDEO, false)
            val url = with(photosUseCase) {
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
        R.string.downloading_original_file,
        NOTIFICATION_ID,
        JOBS_CHANNEL_ID,
        progress
    )

    companion object {
        const val KEY_ID = "id"
        const val KEY_VIDEO = "video"
        fun workName(id: String) = "photoOriginalFileRetrieve/$id"
        private const val NOTIFICATION_ID = 1279
    }
}