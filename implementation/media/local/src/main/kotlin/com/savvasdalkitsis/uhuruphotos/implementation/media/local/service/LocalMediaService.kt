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
package com.savvasdalkitsis.uhuruphotos.implementation.media.local.service

import android.content.ContentResolver
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
import androidx.core.os.CancellationSignal
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.domain.MediaStoreContentUriResolver
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.service.model.LocalMediaPhotoColumns
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.service.model.LocalMediaStoreServiceItem
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.service.model.LocalMediaVideoColumns
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
            dateAdded = long(Images.Media.DATE_ADDED),
            bucketId = int(Images.Media.BUCKET_ID),
            bucketName = string(Images.Media.BUCKET_DISPLAY_NAME),
            width = int(Images.Media.WIDTH),
            height = int(Images.Media.HEIGHT),
            size = int(Images.Media.SIZE),
            contentUri = MediaStoreContentUriResolver.getContentUriForItem(id, video = false),
            path = string(Images.Media.DATA),
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

    private val videoRowHandler: Cursor.() -> LocalMediaStoreServiceItem.Video = {
        val id = long(Video.Media._ID)
        LocalMediaStoreServiceItem.Video(
            id = id,
            displayName = string(Video.Media.DISPLAY_NAME),
            dateAdded = long(Video.Media.DATE_ADDED),
            bucketId = int(Video.Media.BUCKET_ID),
            bucketName = string(Video.Media.BUCKET_DISPLAY_NAME),
            width = int(Video.Media.WIDTH),
            height = int(Video.Media.HEIGHT),
            size = int(Video.Media.SIZE),
            duration = if (SDK_INT >= R) int(Video.Media.DURATION) else null,
            contentUri = MediaStoreContentUriResolver.getContentUriForItem(id, video = true),
            path = string(Video.Media.DATA),
        )
    }

    suspend fun getDefaultBucketId(): Int? {
        val dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        val buckets = query(
            collection = LocalMediaPhotoColumns.collection,
            projection = arrayOf(Images.Media.BUCKET_ID, Images.Media.DATA),
            selection = Images.Media.DATA + " LIKE '${dcim.absolutePath.trim()}%'",
            selectionArgs = null,
        ) {
            int(Images.Media.BUCKET_ID)
        }.toSet() + query(
            collection = LocalMediaVideoColumns.collection,
            projection = arrayOf(Video.Media.BUCKET_ID, Video.Media.DATA),
            selection = Video.Media.DATA + " LIKE '${dcim.absolutePath.trim()}%'",
            selectionArgs = null,
        ) {
            int(Video.Media.BUCKET_ID)
        }.toSet()

        return if (buckets.size == 1) {
            buckets.firstOrNull()
        } else {
            null
        }
    }

    suspend fun getBuckets(): Set<LocalMediaFolder> =
        query(LocalMediaPhotoColumns.collection, arrayOf(Images.Media.BUCKET_ID)) {
            LocalMediaFolder(
                id = int(Images.Media.BUCKET_ID),
                displayName = string(Images.Media.BUCKET_DISPLAY_NAME)
            )
        }.toSet() +
        query(LocalMediaVideoColumns.collection, arrayOf(Video.Media.BUCKET_ID)) {
            LocalMediaFolder(
                id = int(Video.Media.BUCKET_ID),
                displayName = string(Video.Media.BUCKET_DISPLAY_NAME)
            )
        }.toSet()

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

    private fun Cursor.long(col: String) = getLong(getColumnIndexOrThrow(col))
    private fun Cursor.string(col: String) = getString(getColumnIndexOrThrow(col))
    private fun Cursor.int(col: String) = getInt(getColumnIndexOrThrow(col))
    private fun Cursor.bool(col: String) = getInt(getColumnIndexOrThrow(col)) == 1
}