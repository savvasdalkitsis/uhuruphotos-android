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
import android.os.Build
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.date.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.mediastore.MediaStore
import com.savvasdalkitsis.uhuruphotos.api.db.user.User
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalBucket
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalMedia
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalPermissions.Granted
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.LocalPermissions.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.MediaBucket
import com.savvasdalkitsis.uhuruphotos.api.mediastore.model.MediaStoreItem
import com.savvasdalkitsis.uhuruphotos.api.mediastore.usecase.MediaStoreUseCase
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
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
    private val userUseCase: UserUseCase,
    @MediaStoreDateTimeFormat
    private val mediaStoreDateTimeFormat: DateFormat,
    @ParsingDateFormat
    private val parsingDateFormat: DateFormat,
    private val permissionFlow: PermissionFlow,
) : MediaStoreUseCase {

    private val apiPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                    userUseCase.getUserOrRefresh().fold(
                        onFailure = { LocalBucket.Error },
                        onSuccess = { user ->
                            media.toItems()
                                .groupBy(MediaStoreItem::bucket)
                                .mapValues { (_, items) ->
                                    items.toAlbums(user)
                                }
                                .entries
                                .find { entry -> entry.key.id == bucketId }
                                ?.toPair()
                                ?.let(LocalBucket::Found)
                                ?: LocalBucket.Error
                        }
                    )
                }
            }
        }

    override fun observeMedia(): Flow<LocalMedia> =
        observePermissionsState().flatMapLatest { permissions ->
            resetMediaStoreIfNeeded()
            when (permissions) {
                is RequiresPermissions -> flowOf(LocalMedia.RequiresPermissions(permissions.deniedPermissions))
                Granted -> mediaStoreRepository.observeMedia().map { media ->
                    userUseCase.getUserOrRefresh().fold(
                        onFailure = { LocalMedia.Error },
                        onSuccess = { user ->
                            LocalMedia.Found(
                                media.toItems()
                                    .groupBy(MediaStoreItem::bucket)
                                    .mapValues { (_, items) ->
                                        items.toAlbums(user)
                                    }
                            )
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

    override suspend fun getMedia(): Result<List<Album>> =
        userUseCase.getUserOrRefresh().map {
            resetMediaStoreIfNeeded()
            mediaStoreRepository.getMedia().toItems().toAlbums(it)
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

    private fun List<MediaStore>.toItems() = map {
        MediaStoreItem(
            id = it.id,
            displayName = it.displayName,
            dateTaken = it.dateTaken,
            bucket = MediaBucket(id = it.bucketId, it.bucketName),
            width = it.width,
            height = it.height,
            size = it.size,
            contentUri = it.contentUri,
            md5 = it.md5,
            video = it.video,
            duration = it.duration,
            latLon = it.latLon?.split(",")?.let { value ->
                when (value.size) {
                    2 -> value[0].toDoubleOrNull() to value[1].toDoubleOrNull()
                    else -> null
                }
            }?.filterOutNulls(),
            fallbackColor = it.fallbackColor,
        )
    }

    private fun List<MediaStoreItem>.toAlbums(user: User): List<Album> =
            groupBy {
                mediaStoreDateTimeFormat.parse(it.dateTaken)?.let { date ->
                    parsingDateFormat.format(date)
                }
            }.map { (albumDate, items) ->
                Album(
                    id = "local_album_" + albumDate.orEmpty(),
                    displayTitle = albumDate ?: "-",
                    photos = items.map { it.toPhoto(user.id.toString()) },
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