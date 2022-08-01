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

import android.content.ContentResolver
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapFactory.decodeStream
import androidx.palette.graphics.Palette
import com.savvasdalkitsis.api.exif.model.ExifMetadata
import com.savvasdalkitsis.api.exif.usecase.ExifUseCase
import com.savvasdalkitsis.implementation.mediastore.module.MediaStoreModule.MediaStoreDateTimeFormat
import com.savvasdalkitsis.implementation.mediastore.service.model.MediaStoreServiceItem
import com.savvasdalkitsis.implementation.mediastore.service.MediaStoreService
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.await
import com.savvasdalkitsis.uhuruphotos.api.db.mediastore.MediaStore
import com.savvasdalkitsis.uhuruphotos.api.db.mediastore.MediaStoreQueries
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger
import java.security.MessageDigest
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class MediaStoreRepository @Inject constructor(
    private val mediaStoreQueries: MediaStoreQueries,
    private val mediaStoreService: MediaStoreService,
    private val contentResolver: ContentResolver,
    private val exifUseCase: ExifUseCase,
    @MediaStoreDateTimeFormat
    private val dateTimeFormat: DateFormat,
) {

    fun observeMedia(): Flow<List<MediaStore>> = mediaStoreQueries.getItems().asFlow().mapToList()

    fun observeBucket(bucketId: Int): Flow<List<MediaStore>> =
        mediaStoreQueries.getBucketItems(bucketId).asFlow().mapToList()

    suspend fun getMedia(): List<MediaStore> = mediaStoreQueries.getItems().await()

    suspend fun getBucket(bucketId: Int): List<MediaStore> =
        mediaStoreQueries.getBucketItems(bucketId).await()

    suspend fun refreshBucket(bucketId: Int) =
        (mediaStoreService.getPhotosForBucket(bucketId) + mediaStoreService.getVideosForBucket(bucketId))
            .processItems(bucketId)

    suspend fun refresh(
        onProgressChange: suspend (Int) -> Unit = {},
    ) = (mediaStoreService.getPhotos() + mediaStoreService.getVideos())
        .processItems(onProgressChange = onProgressChange)

    private suspend fun <T : MediaStoreServiceItem> List<T>.processItems(
        bucketId: Int? = null,
        onProgressChange: suspend (Int) -> Unit = {},
    ) = process(bucketId, onProgressChange) { item, dataSize, md5, exif, fallbackColor ->
            mediaStoreQueries.insert(
                MediaStore(
                    id = item.id,
                    displayName = item.displayName,
                    dateTaken = exif.dateTime ?: item.dateAdded.toDateString(),
                    bucketId = item.bucketId,
                    bucketName = item.bucketName,
                    width = item.width ?: exif.width ?: 0,
                    height = item.height ?: exif.height ?: 0,
                    size = item.size ?: dataSize,
                    contentUri = item.contentUri.toString(),
                    md5 = md5,
                    video = item is MediaStoreServiceItem.Video,
                    duration = (item as? MediaStoreServiceItem.Video)?.duration,
                    latLon = exif.latLon?.let { (lat, lon) -> "$lat,$lon" },
                    fallbackColor = fallbackColor?.let {
                        "#${it.toUInt().toString(16).padStart(6, '0')}"
                    }
                )
            )
        }

    private suspend fun <T : MediaStoreServiceItem> List<T>.process(
        bucketId: Int? = null,
        onProgressChange: suspend (Int) -> Unit = {},
        onNewItem: (T, dataSize: Int, md5: String, exif: ExifMetadata, fallbackColor: Int?) -> Unit,
    ) {
        onProgressChange(0)
        val existingIds = if (bucketId != null) {
            mediaStoreQueries.getExistingBucketIds(bucketId)
        } else {
            mediaStoreQueries.getExistingIds()
        }.await().toSet()
        val currentIds = map { it.id }.toSet()
        async {
            for (id in existingIds - currentIds) {
                mediaStoreQueries.delete(id)
            }
        }
        val newItems = filter { it.id !in existingIds }
        for ((index, item) in newItems.withIndex()) {
            onProgressChange((100 * ((index + 1)/ newItems.size.toFloat())).toInt())
            async {
                processNewItem(item, onNewItem)
            }
        }
    }

    private fun <T : MediaStoreServiceItem> processNewItem(
        item: T,
        onNewItem: (T, dataSize: Int, md5: String, exif: ExifMetadata, fallbackColor: Int?) -> Unit
    ) {
        val exif = contentResolver.openInputStream(item.contentUri)!!.use { stream ->
            exifUseCase.extractFrom(stream)
        }
        val (size, md5) = contentResolver.openInputStream(item.contentUri)!!.use { stream ->
            val digest = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var totalRead = 0
            do {
                val read = stream.read(buffer)
                if (read > 0) {
                    totalRead += read
                    digest.update(buffer, 0, read)
                }
            } while (read > 0)
            totalRead to BigInteger(1, digest.digest()).toString(16).padStart(32, '0')
        }
        val fallbackColor = if (item is MediaStoreServiceItem.Photo) {
            try {
                contentResolver.openInputStream(item.contentUri)!!.use { stream ->
                    decodeStream(stream, null, Options().apply {
                        outWidth = 100
                    })
                }?.let { Palette.from(it).generate().lightMutedSwatch?.rgb }
            } catch (e: Exception) {
                log(e)
                null
            }
        } else null
        onNewItem(item, size, md5, exif, fallbackColor)
    }

    private fun Long.toDateString(): String =
        dateTimeFormat.format(Date(this * 1000))
}
