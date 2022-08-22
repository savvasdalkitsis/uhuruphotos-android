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
package com.savvasdalkitsis.uhuruphotos.implementation.media.local.domain.usecase

import android.Manifest
import android.os.Build
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.media.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.api.media.local.domain.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.domain.MediaStoreContentUriResolver
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.repository.LocalMediaFolderRepository
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.implementation.media.local.repository.MediaStoreVersionRepository
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import java.text.DateFormat
import javax.inject.Inject

class LocalMediaUseCase @Inject constructor(
    @LocalMediaModule.LocalMediaDateTimeFormat
    private val localMediaDateTimeFormat: DateFormat,
    @ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateFormat,
    @ParsingDateFormat
    private val parsingDateFormat: DateFormat,
    private val dateDisplayer: DateDisplayer,
    private val localMediaRepository: LocalMediaRepository,
    private val permissionFlow: PermissionFlow,
    private val localMediaFolderRepository: LocalMediaFolderRepository,
    private val mediaStoreVersionRepository: MediaStoreVersionRepository,
) : LocalMediaUseCase {

    private val apiPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION)
    } else
        emptyArray()
    private val requiredPermissions = apiPermissions + arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun Long.toContentUri(isVideo: Boolean): String = contentUri(isVideo)

    override suspend fun getLocalMediaItem(id: Long): LocalMediaItem? =
        localMediaRepository.getItem(id)?.toItem()

    override suspend fun refreshLocalMediaItem(id: Long, isVideo: Boolean) =
        localMediaRepository.refreshItem(id, isVideo)

    override suspend fun getLocalMedia(): List<LocalMediaItem> {
        resetMediaStoreIfNeeded()
        return localMediaRepository.getMedia().toItems()
    }

    override fun observeLocalMediaFolder(folderId: Int): Flow<LocalFolder> =
        observePermissionsState().flatMapLatest { permissions ->
            resetMediaStoreIfNeeded()
            when (permissions) {
                is LocalPermissions.RequiresPermissions -> flowOf(LocalFolder.RequiresPermissions(permissions.deniedPermissions))
                else -> localMediaRepository.observeFolder(folderId).map { media ->
                    media.toItems()
                        .groupBy(LocalMediaItem::bucket)
                        .entries
                        .find { entry -> entry.key.id == folderId }
                        ?.toPair()
                        ?.let { (folder, items) ->
                            folder to items.sortedByDescending {
                                it.dateTimeTaken
                            }
                        }
                        ?.let(LocalFolder::Found)
                        ?: LocalFolder.Error
                }
            }
        }

    override fun observeLocalMediaItems(): Flow<LocalMediaItems> =
        observePermissionsState().flatMapLatest { permissions ->
            resetMediaStoreIfNeeded()
            when (permissions) {
                is LocalPermissions.RequiresPermissions -> flowOf(
                    LocalMediaItems.RequiresPermissions(
                        permissions.deniedPermissions
                    )
                )
                LocalPermissions.Granted -> localMediaRepository.observeMedia().map { itemDetails ->
                    val defaultBucket = getDefaultBucketId()
                    val media = itemDetails.toItems()
                            .groupBy(LocalMediaItem::bucket)
                    LocalMediaItems.Found(
                        primaryLocalMediaFolder = media.entries.firstOrNull { (folder, _) ->
                            folder.id == defaultBucket
                        }?.toPair(),
                        localMediaFolders = media.filter { (folder, _) ->
                            folder.id != defaultBucket
                        }.toList().sortedBy { (folder, _) -> folder.displayName },
                    )
                }
            }
        }

    override suspend fun refreshLocalMediaFolder(folderId: Int) = runCatchingWithLog {
        resetMediaStoreIfNeeded()
        localMediaRepository.refreshFolder(folderId)
    }

    override fun observePermissionsState(): Flow<LocalPermissions>  =
        permissionFlow.getMultiplePermissionState(*requiredPermissions).mapLatest {
            when {
                !it.allGranted -> LocalPermissions.RequiresPermissions(it.deniedPermissions)
                else -> LocalPermissions.Granted
            }
        }

    override suspend fun refreshAll(
        onProgressChange: suspend (Int) -> Unit,
    ) {
        resetMediaStoreIfNeeded()
        localMediaRepository.refresh(onProgressChange)
    }

    private fun Long.contentUri(isVideo: Boolean) =
        MediaStoreContentUriResolver.getContentUriForItem(this, isVideo).toString()

    private fun List<LocalMediaItemDetails>.toItems() = map { it.toItem() }

    private fun LocalMediaItemDetails.toItem(): LocalMediaItem {
        val date = localMediaDateTimeFormat.parse(dateTaken)
        val dateTimeString = date!!.let {
            parsingDateTimeFormat.format(it)
        }
        return LocalMediaItem(
            id = id,
            displayName = displayName,
            displayDate = dateDisplayer.dateString(dateTimeString),
            displayDateTime = dateDisplayer.dateTimeString(dateTimeString),
            dateTimeTaken = parsingDateTimeFormat.format(date),
            dateTaken = parsingDateFormat.format(date),
            bucket = LocalMediaFolder(id = bucketId, bucketName),
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
            path = path,
        )
    }

    private fun <T> Pair<T?, T?>.filterOutNulls(): Pair<T, T>? {
        val (f, s) = this
        return if (f != null && s != null)
            f to s
        else
            null
    }

    private suspend fun getDefaultBucketId(): Int? {
        resetMediaStoreIfNeeded()
        return localMediaFolderRepository.getDefaultLocalFolderId()
    }

    private suspend fun resetMediaStoreIfNeeded() {
        with (mediaStoreVersionRepository) {
            if (latestMediaStoreVersion != currentMediaStoreVersion) {
                async {
                    localMediaRepository.clearAll()
                }
                currentMediaStoreVersion = latestMediaStoreVersion
            }
        }
    }

}