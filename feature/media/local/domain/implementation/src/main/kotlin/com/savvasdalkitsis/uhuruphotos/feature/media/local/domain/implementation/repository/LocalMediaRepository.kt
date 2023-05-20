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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import android.content.ContentResolver
import android.graphics.BitmapFactory.Options
import android.graphics.BitmapFactory.decodeStream
import androidx.palette.graphics.Palette
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.await
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionException
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.usecase.ExifUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.joda.time.format.DateTimeFormatter
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

class LocalMediaRepository @Inject constructor(
    private val database: Database,
    private val localMediaItemDetailsQueries: LocalMediaItemDetailsQueries,
    private val downloadingMediaItemsQueries: DownloadingMediaItemsQueries,
    private val localMediaService: LocalMediaService,
    private val contentResolver: ContentResolver,
    private val exifUseCase: ExifUseCase,
    @LocalMediaModule.LocalMediaDateTimeFormat
    private val dateTimeFormat: DateTimeFormatter,
) {

    fun observeMedia(): Flow<List<LocalMediaItemDetails>> = localMediaItemDetailsQueries.getItems()
        .asFlow().mapToList().distinctUntilChanged()

    fun observeFolder(folderId: Int): Flow<List<LocalMediaItemDetails>> =
        localMediaItemDetailsQueries.getBucketItems(folderId).asFlow().mapToList().distinctUntilChanged()

    suspend fun getMedia(): List<LocalMediaItemDetails> = localMediaItemDetailsQueries.getItems().await()

    suspend fun refreshFolder(folderId: Int) =
        (localMediaService.getPhotosForBucket(folderId) + localMediaService.getVideosForBucket(folderId))
            .processAndInsertItems(folderId)

    suspend fun refresh(
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    ) {
        val camera = localMediaService.getDefaultBucketId()
        val cameraPhotos = (camera?.let { localMediaService.getPhotosForBucket(it) } ?: emptyList())
            .sortedByDescending { it.dateTaken }
        val cameraVideos = (camera?.let { localMediaService.getVideosForBucket(it) } ?: emptyList())
            .sortedByDescending { it.dateTaken }
        val photos = localMediaService.getPhotos() - cameraPhotos.toSet()
        val videos = localMediaService.getVideos() - cameraVideos.toSet()
        val cameraItems = (cameraPhotos + cameraVideos).sortedByDescending { it.dateTaken }
        (cameraItems + photos + videos)
            .processAndInsertItems(onProgressChange = onProgressChange)
    }

    private suspend fun <T : LocalMediaStoreServiceItem> List<T>.processAndInsertItems(
        bucketId: Int? = null,
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
        removeMissingItems: Boolean = true,
        forceProcess: Boolean = false,
    ) = process(bucketId, onProgressChange, removeMissingItems, forceProcess) { itemDetails ->
        database.transaction {
            downloadingMediaItemsQueries.removeStartingWith(itemDetails.md5)
            localMediaItemDetailsQueries.insert(itemDetails)
        }
    }

    private suspend fun <T : LocalMediaStoreServiceItem> List<T>.process(
        bucketId: Int? = null,
        onProgressChange: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
        removeMissingItems: Boolean = true,
        forceProcess: Boolean = false,
        onNewItem: (LocalMediaItemDetails) -> Unit,
    ) {
        onProgressChange(0, 0)
        val existingIds = when {
            forceProcess -> emptySet()
            else -> if (bucketId != null) {
                localMediaItemDetailsQueries.getExistingBucketIds(bucketId)
            } else {
                localMediaItemDetailsQueries.getExistingIds()
            }.await().toSet()
        }
        if (removeMissingItems) {
            val currentIds = map { it.id }.toSet()
            async {
                for (id in existingIds - currentIds) {
                    removeItemsFromDb(id)
                }
            }
        }
        val newItems = filter { it.id !in existingIds }
        for ((index, item) in newItems.withIndex()) {
            onProgressChange(index, newItems.size)
            async {
                processNewItem(item, onNewItem)
            }
        }
    }

    private fun <T : LocalMediaStoreServiceItem> processNewItem(
        item: T,
        onNewItem: (LocalMediaItemDetails) -> Unit
    ) {
        val exif = contentResolver.openInputStream(item.contentUri)!!.use { stream ->
            exifUseCase.extractFrom(stream)
        }
        val (size, md5) = contentResolver.openInputStream(item.contentUri)!!.use { stream ->
            val digest = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(64 * 1024)
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
        val fallbackColor = if (item is Photo) {
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
        onNewItem(
            LocalMediaItemDetails(
                id = item.id,
                displayName = item.displayName,
                dateTaken = item.dateTaken.toDateString(),
                bucketId = item.bucketId,
                bucketName = item.bucketName,
                width = item.width ?: 0,
                height = item.height ?: 0,
                size = item.size ?: size,
                contentUri = item.contentUri.toString(),
                md5 = md5,
                video = item is LocalMediaStoreServiceItem.Video,
                duration = (item as? LocalMediaStoreServiceItem.Video)?.duration,
                latLon = exif.latLon?.let { (lat, lon) -> "$lat,$lon" },
                fallbackColor = fallbackColor?.let {
                    "#${it.toUInt().toString(16).padStart(6, '0')}"
                },
                path = item.path,
                orientation = item.orientation,
            )
        )
    }

    private fun Long.toDateString(): String =
        dateTimeFormat.print(this)

    fun clearAll() {
        localMediaItemDetailsQueries.clearAll()
    }

    suspend fun refreshItem(id: Long, video: Boolean) = runCatchingWithLog {
        when {
            video -> localMediaService.getVideosForId(id)
            else -> localMediaService.getPhotosForId(id)
        }.processAndInsertItems(
            removeMissingItems = false,
            forceProcess = true,
        )
    }

    fun observeItem(id: Long): Flow<LocalMediaItemDetails> =
        localMediaItemDetailsQueries.getItem(id).asFlow().mapToOneNotNull().distinctUntilChanged()

    suspend fun getItem(id: Long): LocalMediaItemDetails? =
        localMediaItemDetailsQueries.getItem(id).awaitSingleOrNull()

    fun deletePhotos(vararg ids: Long) = runCatchingWithLog {
        if (localMediaService.deletePhotos(*ids) > 0) {
            removeItemsFromDb(*ids)
        } else {
            throw LocalMediaDeletionException(*ids)
        }
     }

    fun deleteVideos(vararg ids: Long) = runCatchingWithLog {
        if (localMediaService.deleteVideos(*ids) > 0) {
            removeItemsFromDb(*ids)
        } else {
            throw LocalMediaDeletionException(*ids)
        }
     }

    fun removeItemsFromDb(vararg ids: Long) =
        localMediaItemDetailsQueries.delete(ids.toList())
}
