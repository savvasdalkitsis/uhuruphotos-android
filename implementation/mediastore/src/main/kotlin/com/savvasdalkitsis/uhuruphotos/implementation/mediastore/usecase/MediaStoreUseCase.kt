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
package com.savvasdalkitsis.uhuruphotos.implementation.mediastore.usecase

import android.Manifest.permission.ACCESS_MEDIA_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.date.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.mediastore.MediaStore
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalBucket
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalMedia
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalPermissions.Granted
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalPermissions.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.MediaBucket
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.MediaStoreItem
import com.savvasdalkitsis.uhuruphotos.api.mediastore.usecase.MediaStoreUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.mediastore.module.MediaStoreModule.MediaStoreDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.implementation.mediastore.repository.MediaStoreBucketRepository
import com.savvasdalkitsis.uhuruphotos.implementation.mediastore.repository.MediaStoreRepository
import com.savvasdalkitsis.uhuruphotos.implementation.mediastore.repository.MediaStoreVersionRepository
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import java.text.DateFormat
import javax.inject.Inject

class MediaStoreUseCase @Inject constructor(
    private val mediaStoreRepository: MediaStoreRepository,
    private val mediaStoreBucketRepository: MediaStoreBucketRepository,
    private val mediaStoreVersionRepository: MediaStoreVersionRepository,
    @MediaStoreDateTimeFormat
    private val mediaStoreDateTimeFormat: DateFormat,
    @ParsingDateFormat
    private val parsingDateFormat: DateFormat,
    private val permissionFlow: PermissionFlow,
) : MediaStoreUseCase {

    private val apiPermissions = if (SDK_INT >= Q) {
        arrayOf(ACCESS_MEDIA_LOCATION)
    } else
        emptyArray()
    private val requiredPermissions = apiPermissions + arrayOf(READ_EXTERNAL_STORAGE)

    override suspend fun getDefaultBucketId(): Int? {
        resetMediaStoreIfNeeded()
        return mediaStoreBucketRepository.getDefaultBucketId()
    }

    override fun setDefaultBucketId(bucketId: Int) {
        mediaStoreBucketRepository.setDefaultBucketId(bucketId)
    }

    override fun observeBuckets(): Flow<Set<MediaBucket>> = flow {
        resetMediaStoreIfNeeded()
        emitAll(mediaStoreBucketRepository.observeBuckets())
    }

    override suspend fun getBuckets(): Set<MediaBucket> {
        resetMediaStoreIfNeeded()
        return mediaStoreBucketRepository.getBuckets()
    }

    override fun observeBucket(bucketId: Int): Flow<LocalBucket> =
        observePermissionsState().flatMapLatest { permissions ->
            resetMediaStoreIfNeeded()
            when (permissions) {
                is RequiresPermissions -> flowOf(LocalBucket.RequiresPermissions(permissions.deniedPermissions))
                else -> mediaStoreRepository.observeBucket(bucketId).map { media ->
                    media.toItems()
                        .groupBy(MediaStoreItem::bucket)
                        .mapValues { (_, items) ->
                            items.toAlbums()
                        }
                        .entries
                        .find { entry -> entry.key.id == bucketId }
                        ?.toPair()
                        ?.let(LocalBucket::Found)
                        ?: LocalBucket.Error
                }
            }
        }

    override fun observeMedia(): Flow<LocalMedia> =
        observePermissionsState().flatMapLatest { permissions ->
            resetMediaStoreIfNeeded()
            when (permissions) {
                is RequiresPermissions -> flowOf(LocalMedia.RequiresPermissions(permissions.deniedPermissions))
                Granted -> mediaStoreRepository.observeMedia().map { media ->
                    LocalMedia.Found(
                        media.toItems()
                            .groupBy(MediaStoreItem::bucket)
                            .mapValues { (_, items) ->
                                items.toAlbums()
                            }
                    )
                }
            }
        }

    override fun observePermissionsState(): Flow<LocalPermissions>  =
        permissionFlow.getMultiplePermissionState(*requiredPermissions).mapLatest {
            when {
                !it.allGranted -> RequiresPermissions(it.deniedPermissions)
                else -> Granted
            }
        }

    override suspend fun refreshItem(id: Int, video: Boolean) {
        mediaStoreRepository.refreshItem(id, video)
    }

    override suspend fun getItem(id: Int, isVideo: Boolean): MediaStoreItem? =
        mediaStoreRepository.getItem(id, isVideo)?.toItem()


    override suspend fun getMedia(): List<Album> {
        resetMediaStoreIfNeeded()
        return mediaStoreRepository.getMedia().toItems().toAlbums()
    }

    override suspend fun refresh(
        onProgressChange: suspend (Int) -> Unit,
    ) {
        resetMediaStoreIfNeeded()
        mediaStoreRepository.refresh(onProgressChange)
    }

    override suspend fun refreshBucket(bucketId: Int) {
        resetMediaStoreIfNeeded()
        mediaStoreRepository.refreshBucket(bucketId)
    }

    private fun List<MediaStore>.toItems() = map { it.toItem() }

    private fun MediaStore.toItem() = MediaStoreItem(
        id = id,
        displayName = displayName,
        dateTaken = dateTaken,
        bucket = MediaBucket(id = bucketId, bucketName),
        width = width,
        height = height,
        size = size,
        contentUri = contentUri,
        md5 = md5,
        video = video,
        duration = duration,
        latLon = latLon?.split(",")?.let { value ->
            when (value.size) {
                2 -> value[0].toDoubleOrNull() to value[1].toDoubleOrNull()
                else -> null
            }
        }?.filterOutNulls(),
        fallbackColor = fallbackColor,
    )

    private fun List<MediaStoreItem>.toAlbums(): List<Album> =
            groupBy {
                mediaStoreDateTimeFormat.parse(it.dateTaken)?.let { date ->
                    parsingDateFormat.format(date)
                }
            }.map { (albumDate, items) ->
                Album(
                    id = "local_album_" + albumDate.orEmpty(),
                    displayTitle = albumDate ?: "-",
                    photos = items.map { it.toPhoto() },
                    location = null,
                )
            }

    private fun <T> Pair<T?, T?>.filterOutNulls(): Pair<T, T>? {
        val (f, s) = this
        return if (f != null && s != null)
            f to s
        else
            null
    }

    private suspend fun resetMediaStoreIfNeeded() {
        with (mediaStoreVersionRepository) {
            if (latestMediaStoreVersion != currentMediaStoreVersion) {
                async {
                    mediaStoreRepository.clearAll()
                }
                currentMediaStoreVersion = latestMediaStoreVersion
            }
        }
    }
}