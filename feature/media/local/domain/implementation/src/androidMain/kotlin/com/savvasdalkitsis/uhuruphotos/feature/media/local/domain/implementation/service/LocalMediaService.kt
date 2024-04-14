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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.R
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Video
import androidx.core.content.ContentResolverCompat
import androidx.core.database.getStringOrNull
import androidx.core.os.CancellationSignal
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.MediaStoreContentUriResolver
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaPhotoColumns
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaVideoColumns
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class LocalMediaService @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val contentResolver: ContentResolver,
) {

    val mediaStoreCurrentVersion
        get() = MediaStore.getVersion(context)

    suspend fun getPhotos(): List<LocalMediaStoreServiceItem.Photo> =
        query(
            collection = LocalMediaPhotoColumns.collection,
            projection = LocalMediaPhotoColumns.projection,
            rowHandler = photoRowHandler,
        )

    suspend fun getPhotosForBucket(bucketId: Int): List<LocalMediaStoreServiceItem.Photo> =
        query(
            collection = LocalMediaPhotoColumns.collection,
            projection = LocalMediaPhotoColumns.projection,
            selection = Images.Media.BUCKET_ID + " = ?",
            selectionArgs = arrayOf(bucketId.toString()),
            rowHandler = photoRowHandler,
        )

    suspend fun getPhotosForId(photoId: Long): List<LocalMediaStoreServiceItem.Photo> =
        query(
            collection = LocalMediaPhotoColumns.collection,
            projection = LocalMediaPhotoColumns.projection,
            selection = Images.Media._ID + " = ?",
            selectionArgs = arrayOf(photoId.toString()),
            rowHandler = photoRowHandler,
        )

    private val photoRowHandler: Cursor.() -> LocalMediaStoreServiceItem.Photo = {
        val id = long(Images.Media._ID)
        LocalMediaStoreServiceItem.Photo(
            id = id,
            displayName = string(Images.Media.DISPLAY_NAME),
            dateTaken = long(Images.Media.DATE_TAKEN).orFrom { long(Images.Media.DATE_ADDED) * 1000 },
            bucketId = int(Images.Media.BUCKET_ID),
            bucketName = nullableString(Images.Media.BUCKET_DISPLAY_NAME) ?: "-",
            width = int(Images.Media.WIDTH),
            height = int(Images.Media.HEIGHT),
            size = int(Images.Media.SIZE),
            contentUri = MediaStoreContentUriResolver.getContentUriForItem(id, video = false),
            path = string(Images.Media.DATA),
            orientation = nullableString(Images.Media.ORIENTATION),
        )
    }

    suspend fun getVideos(): List<LocalMediaStoreServiceItem.Video> =
        query(
            collection = LocalMediaVideoColumns.collection,
            projection = LocalMediaVideoColumns.projection,
            rowHandler = videoRowHandler,
        )

    suspend fun getVideosForBucket(bucketId: Int): List<LocalMediaStoreServiceItem.Video> =
        query(
            collection = LocalMediaVideoColumns.collection,
            projection = LocalMediaVideoColumns.projection,
            selection = Video.Media.BUCKET_ID + " = ?",
            selectionArgs = arrayOf(bucketId.toString()),
            rowHandler = videoRowHandler,
        )

    suspend fun getVideosForId(videoId: Long): List<LocalMediaStoreServiceItem.Video> =
        query(
            collection = LocalMediaVideoColumns.collection,
            projection = LocalMediaVideoColumns.projection,
            selection = Video.Media._ID + " = ?",
            selectionArgs = arrayOf(videoId.toString()),
            rowHandler = videoRowHandler,
        )

    fun deletePhotos(vararg ids: Long) =
        contentResolver.delete(LocalMediaPhotoColumns.collection,
            Images.Media._ID + " in (${ids.joinToString(",") { "?" }})",
            ids.map { it.toString() }.toTypedArray(),
        )

    fun deleteVideos(vararg ids: Long) =
        contentResolver.delete(LocalMediaVideoColumns.collection,
            Video.Media._ID + " in (${ids.joinToString(",") { "?" }})",
            ids.map { it.toString() }.toTypedArray(),
        )

    fun createMediaItemUri(id: Long, video: Boolean): Uri = ContentUris.withAppendedId(
        when {
            video -> LocalMediaVideoColumns.collection
            else -> LocalMediaPhotoColumns.collection
        }, id)

    private val videoRowHandler: Cursor.() -> LocalMediaStoreServiceItem.Video = {
        val id = long(Video.Media._ID)
        LocalMediaStoreServiceItem.Video(
            id = id,
            displayName = string(Video.Media.DISPLAY_NAME),
            dateTaken = long(Video.Media.DATE_TAKEN).orFrom { long(Video.Media.DATE_ADDED) },
            bucketId = int(Video.Media.BUCKET_ID),
            bucketName = nullableString(Video.Media.BUCKET_DISPLAY_NAME) ?: "-",
            width = int(Video.Media.WIDTH),
            height = int(Video.Media.HEIGHT),
            size = int(Video.Media.SIZE),
            duration = if (SDK_INT >= R) int(Video.Media.DURATION) else null,
            contentUri = MediaStoreContentUriResolver.getContentUriForItem(id, video = true),
            path = string(Video.Media.DATA),
            orientation = nullableString(Video.Media.ORIENTATION),
        )
    }

    suspend fun getDefaultBucketId(): Int? {
        val dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val buckets = queryBucket(dcim.absolutePath.trim())

        return if (buckets.size == 1) {
            buckets.entries.firstOrNull()?.key
        } else {
            val entries = queryBucket(dcim.absolutePath.trim() + "/Camera").entries
            entries.find { (_, name) ->
                name.endsWith("Camera")
            }?.key
        }
    }

    private suspend fun queryBucket(
        path: String
    ): Map<Int, String> {
        val buckets = query(
            collection = LocalMediaPhotoColumns.collection,
            projection = arrayOf(Images.Media.BUCKET_ID, Images.Media.DATA, Images.Media.BUCKET_DISPLAY_NAME),
            selection = Images.Media.DATA + " LIKE '$path%'",
            selectionArgs = null,
        ) {
            int(Images.Media.BUCKET_ID) to nullableString(Images.Media.BUCKET_DISPLAY_NAME).orEmpty()
        }.toSet() + query(
            collection = LocalMediaVideoColumns.collection,
            projection = arrayOf(Video.Media.BUCKET_ID, Video.Media.DATA, Video.Media.BUCKET_DISPLAY_NAME),
            selection = Video.Media.DATA + " LIKE '$path%'",
            selectionArgs = null,
        ) {
            int(Video.Media.BUCKET_ID) to nullableString(Video.Media.BUCKET_DISPLAY_NAME).orEmpty()
        }.toSet()
        return buckets.toMap()
    }

    private suspend fun <T> query(
        collection: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<out String>? = null,
        rowHandler: Cursor.() -> T,
    ) = suspendCancellableCoroutine<List<T>> { cont ->
        log("MediaStore") { "Querying uri: $collection for selection $selection" }
        val cancellation = CancellationSignal()
        val result = mutableListOf<T>()
        ContentResolverCompat.query(
            contentResolver,
            collection,
            projection,
            selection,
            selectionArgs,
            null,
            cancellation,
        )?.use {
            log("MediaStore") { "Found ${it.count} items for $collection" }
            while(it.moveToNext()) {
                result += rowHandler(it).apply {
                    log("MediaStore") { "Found item $this" }
                }
            }
        }
        cont.invokeOnCancellation {
            it?.let { log(it) }
            cancellation.cancel()
        }
        cont.resume(result) {
            log(it)
            cancellation.cancel()
        }
    }

    private fun Cursor.long(col: String) = tryGet(col, ::getLong)
    private fun Cursor.nullableString(col: String) = try {
        getStringOrNull(getColumnIndexOrThrow(col))
    } catch (e: Exception) {
        log(e)
        null
    }
    private fun Cursor.string(col: String) = tryGet(col, ::getString)
    private fun Cursor.int(col: String) = tryGet(col, ::getInt)
    private fun <T> Cursor.tryGet(col: String, value: (Int) -> T) : T =
        try {
            value(getColumnIndexOrThrow(col)) ?:
            throw IllegalStateException("Could not get column: $col")
        } catch (e: Exception) {
            throw IllegalStateException("Could not get column: $col", e)
        }
    private fun Long.orFrom(block: () -> Long) = this.takeIf { it > 0 } ?: block()
}