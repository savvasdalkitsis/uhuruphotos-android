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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.StoragePermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.MediaStoreContentUriResolver
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaFolderRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.MediaStoreVersionRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker.LocalMediaSyncWorker
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class LocalMediaUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @LocalMediaModule.LocalMediaDateTimeFormat
    private val localMediaDateTimeFormat: DateTimeFormatter,
    @ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateTimeFormatter,
    @ParsingDateFormat
    private val parsingDateFormat: DateTimeFormatter,
    private val dateDisplayer: DateDisplayer,
    private val localMediaRepository: LocalMediaRepository,
    private val permissionFlow: PermissionFlow,
    private val localMediaFolderRepository: LocalMediaFolderRepository,
    private val mediaStoreVersionRepository: MediaStoreVersionRepository,
    private val workerStatusUseCase: WorkerStatusUseCase,
) : LocalMediaUseCase {

    private val apiQPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION)
    } else
        emptyArray()
    private val apiTPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    } else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    private val requiredPermissions = apiQPermissions + apiTPermissions
    private val requiredDeletePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        emptyArray()
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override fun Long.toContentUri(isVideo: Boolean): String = contentUri(isVideo)

    override suspend fun getLocalMediaItem(id: Long): LocalMediaItem? =
        localMediaRepository.getItem(id)?.toItem()

    override suspend fun refreshLocalMediaItem(id: Long, isVideo: Boolean) =
        localMediaRepository.refreshItem(id, isVideo)

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
                    foundLocalMediaItems(itemDetails)
                }
            }
        }

    override suspend fun getLocalMediaItems(): LocalMediaItems {
        resetMediaStoreIfNeeded()
        return when (val permissions = observePermissionsState().first()) {
            is LocalPermissions.RequiresPermissions -> LocalMediaItems.RequiresPermissions(
                permissions.deniedPermissions
            )
            LocalPermissions.Granted -> foundLocalMediaItems(localMediaRepository.getMedia())
        }
    }

    private suspend fun foundLocalMediaItems(
        itemDetails: List<LocalMediaItemDetails>
    ): LocalMediaItems.Found {
        val defaultBucket = getDefaultBucketId()
        val media = itemDetails.toItems()
            .groupBy(LocalMediaItem::bucket)
        return LocalMediaItems.Found(
            primaryLocalMediaFolder = media.entries.firstOrNull { (folder, _) ->
                folder.id == defaultBucket
            }?.toPair(),
            localMediaFolders = media.filter { (folder, _) ->
                folder.id != defaultBucket
            }.toList().sortedBy { (folder, _) -> folder.displayName },
        )
    }

    override suspend fun refreshLocalMediaFolder(folderId: Int) = runCatchingWithLog {
        resetMediaStoreIfNeeded()
        localMediaRepository.refreshFolder(folderId)
    }

    override fun observePermissionsState(): Flow<LocalPermissions>  =
        observePermissionsState(*requiredPermissions)

    private fun observePermissionsState(vararg permissions: String): Flow<LocalPermissions>  =
        permissionFlow.getMultiplePermissionState(*permissions).mapLatest {
            when {
                !it.allGranted -> LocalPermissions.RequiresPermissions(it.deniedPermissions)
                else -> LocalPermissions.Granted
            }
        }

    override fun observeLocalMediaSyncJobStatus(): Flow<WorkInfo.State?> =
        workerStatusUseCase.monitorUniqueJobStatus(LocalMediaSyncWorker.WORK_NAME)

    override suspend fun refreshAll(
        onProgressChange: suspend (current: Int, total: Int) -> Unit,
    ) {
        resetMediaStoreIfNeeded()
        localMediaRepository.refresh(onProgressChange)
    }

    override suspend fun deleteLocalMediaItem(id: Long, video: Boolean): LocalMediaItemDeletion =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                when {
                    Environment.isExternalStorageManager() -> attemptDeletion(id, video)
                    else -> LocalMediaItemDeletion.RequiresManageFilesAccess(
                        StoragePermissionRequest(
                            intent = Intent(
                                ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            ),
                            fallbackIntent = Intent(
                                ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
                            )
                        )
                    )
                }
            else -> when (val permissions = observePermissionsState(*requiredDeletePermissions).first()) {
                is LocalPermissions.RequiresPermissions -> LocalMediaItemDeletion.RequiresPermissions(
                    permissions.deniedPermissions
                )
                LocalPermissions.Granted -> attemptDeletion(id, video)
            }
        }

    private fun attemptDeletion(
        id: Long,
        video: Boolean
    ): LocalMediaItemDeletion {
        val result = localMediaRepository.deleteItem(id, video)
        return when {
            result.isSuccess -> LocalMediaItemDeletion.Success
            else -> LocalMediaItemDeletion.Error(
                result.exceptionOrNull() ?: UnknownError()
            )
        }
    }

    private fun Long.contentUri(isVideo: Boolean) =
        MediaStoreContentUriResolver.getContentUriForItem(this, isVideo).toString()

    private fun List<LocalMediaItemDetails>.toItems() = map { it.toItem() }

    private fun LocalMediaItemDetails.toItem(): LocalMediaItem {
        val date = localMediaDateTimeFormat.parseDateTime(dateTaken)
        val dateTimeString = parsingDateTimeFormat.print(date)
        return LocalMediaItem(
            id = id,
            displayName = displayName,
            displayDate = dateDisplayer.dateString(dateTimeString),
            displayDateTime = dateDisplayer.dateTimeString(dateTimeString),
            dateTimeTaken = parsingDateTimeFormat.print(date),
            dateTaken = parsingDateFormat.print(date),
            sortableDate = parsingDateTimeFormat.print(date),
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