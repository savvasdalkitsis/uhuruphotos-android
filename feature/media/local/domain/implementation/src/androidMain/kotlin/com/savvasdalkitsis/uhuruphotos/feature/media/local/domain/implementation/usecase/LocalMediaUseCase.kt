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
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Environment
import android.provider.MediaStore.Images
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.MIME_TYPE
import android.provider.MediaStore.MediaColumns.RELATIVE_PATH
import androidx.core.content.ContextCompat
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.Granted
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions.RequiresPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.MediaStoreContentUriResolver
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaFolderRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.MediaStoreVersionRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker.LocalMediaSyncWorker
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model.RefreshJobState
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import dev.shreyaspatil.permissionFlow.PermissionFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.joda.time.format.DateTimeFormatter
import java.io.IOException

class LocalMediaUseCase(
    private val context: Context,
    private val localMediaDateTimeFormat: DateTimeFormatter,
    private val parsingDateTimeFormat: DateTimeFormatter,
    private val parsingDateFormat: DateTimeFormatter,
    private val dateDisplayer: DateDisplayer,
    private val localMediaRepository: LocalMediaRepository,
    private val permissionFlow: PermissionFlow,
    private val localMediaFolderRepository: LocalMediaFolderRepository,
    private val mediaStoreVersionRepository: MediaStoreVersionRepository,
    private val workerStatusUseCase: WorkerStatusUseCase,
    private val bitmapUseCase: BitmapUseCase,
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
) : LocalMediaUseCase {

    private val apiQPermissions = if (SDK_INT >= Q) {
        arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION)
    } else
        emptyArray()
    private val apiTPermissions = if (SDK_INT >= TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
        )
    } else
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    private val requiredPermissions = apiQPermissions + apiTPermissions
    private val resolver get() = context.contentResolver

    override fun Long.toContentUri(isVideo: Boolean): String = contentUri(isVideo)

    override fun observeLocalMediaItem(id: Long): Flow<LocalMediaItem> =
        localMediaRepository.observeItem(id).map { it.toItem() }

    override suspend fun getLocalMediaItem(id: Long): LocalMediaItem? =
        localMediaRepository.getItem(id)?.toItem()

    override suspend fun refreshLocalMediaItem(id: Long, isVideo: Boolean) =
        localMediaRepository.refreshItem(id, isVideo)

    override fun observeLocalMediaFolder(folderId: Int): Flow<LocalFolder> =
        observePermissionsState().flatMapLatest { permissions ->
            resetMediaStoreIfNeeded()
            when (permissions) {
                is RequiresPermissions -> flowOf(LocalFolder.RequiresPermissions(permissions.deniedPermissions))
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
                is RequiresPermissions -> flowOf(
                    LocalMediaItems.RequiresPermissions(
                        permissions.deniedPermissions
                    )
                )
                Granted -> localMediaRepository.observeMedia()
                    .distinctUntilChanged().map { itemDetails ->
                        foundLocalMediaItems(itemDetails)
                    }
            }
        }

    override suspend fun getLocalMediaItems(): LocalMediaItems {
        resetMediaStoreIfNeeded()
        return when (val permissions = checkPermissions(*requiredPermissions)) {
            is RequiresPermissions -> LocalMediaItems.RequiresPermissions(
                permissions.deniedPermissions
            )
            Granted -> foundLocalMediaItems(localMediaRepository.getMedia())
        }
    }

    private suspend fun foundLocalMediaItems(
        itemDetails: List<LocalMediaItemDetails>
    ): LocalMediaItems.Found {
        val defaultFolder = getDefaultFolderId()
        val media = itemDetails.toItems()
            .groupBy(LocalMediaItem::bucket)
        return LocalMediaItems.Found(
            primaryLocalMediaFolder = media.entries.firstOrNull { (folder, _) ->
                folder.id == defaultFolder
            }?.toPair(),
            localMediaFolders = media.filter { (folder, _) ->
                folder.id != defaultFolder
            }.toList().sortedBy { (folder, _) -> folder.displayName },
        )
    }

    override suspend fun refreshLocalMediaFolder(folderId: Int) = runCatchingWithLog {
        resetMediaStoreIfNeeded()
        localMediaRepository.refreshFolder(folderId)
    }.simple()

    override fun observePermissionsState(): Flow<LocalPermissions>  =
        observePermissionsState(*requiredPermissions)

    private fun observePermissionsState(vararg permissions: String): Flow<LocalPermissions>  =
        permissionFlow.getMultiplePermissionState(*permissions).map {
            when {
                !it.allGranted -> RequiresPermissions(it.deniedPermissions)
                else -> Granted
            }
        }

    override fun observeLocalMediaSyncJob(): Flow<RefreshJobState?> =
        workerStatusUseCase.monitorUniqueJob(LocalMediaSyncWorker.WORK_NAME).map {
            it?.let { work ->
                RefreshJobState(
                    status = work.state,
                    progress = ForegroundNotificationWorker.getProgressOf(work)
                )
            }
        }

    override suspend fun savePhoto(
        bitmap: Bitmap,
        name: String,
        originalFileUri: Uri?,
    ): Boolean {
        val values = ContentValues().apply {
            put(DISPLAY_NAME, name)
            put(MIME_TYPE, "image/jpeg")
            put(RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        return when (val uri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)) {
            null -> {
                log { "Failed to create new MediaStore record." }
                false
            }
            else -> try {
                val bytes = with(bitmapUseCase) {
                    bitmap.toJpeg().copyExifFrom(originalFileUri)
                }
                resolver.openOutputStream(uri)?.use {
                    it.write(bytes)
                } ?: throw IOException("Failed to open output stream.")
                true
            } catch (e: Exception) {
                log(e)
                resolver.delete(uri, null, null)
                false
            }
        }
    }

    override suspend fun refreshAll(
        onProgressChange: suspend (current: Int, total: Int) -> Unit,
    ) {
        val permissions = checkPermissions(*requiredPermissions)
        if (permissions is RequiresPermissions){
            throw IllegalStateException("App does not have permissions: $permissions")
        }
        resetMediaStoreIfNeeded()
        localMediaRepository.refresh(onProgressChange)
    }

    override fun startScanningOtherFolders() {
        localMediaFolderRepository.setScanningOtherFolders(true)
        localMediaWorkScheduler.scheduleLocalMediaSyncNow()
    }

    override fun doNotScanOtherFolders() {
        localMediaFolderRepository.setScanningOtherFolders(false)
    }

    override fun areOtherFoldersBeingScanned(): Flow<Boolean> =
        localMediaFolderRepository.observeScanningOtherFolders()

    private fun checkPermissions(vararg permissions: String): LocalPermissions {
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PERMISSION_GRANTED
        }
        return if (missing.isEmpty()) {
            Granted
        } else {
            RequiresPermissions(missing)
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
            thumbnailPath = thumbnailPath,
            md5 = Md5Hash(md5),
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
            orientation = when (orientation) {
                "0" -> MediaOrientation.ORIENTATION_0
                "90" -> MediaOrientation.ORIENTATION_90
                "180" -> MediaOrientation.ORIENTATION_180
                "270" -> MediaOrientation.ORIENTATION_270
                else -> MediaOrientation.ORIENTATION_UNKNOWN
            },
        )
    }

    private fun <T> Pair<T?, T?>.filterOutNulls(): Pair<T, T>? {
        val (f, s) = this
        return if (f != null && s != null)
            f to s
        else
            null
    }

    override suspend fun getDefaultFolderId(): Int? {
        resetMediaStoreIfNeeded()
        return localMediaFolderRepository.getDefaultLocalFolderId()
    }

    override fun clearAll() {
        localMediaRepository.clearAll()
    }

    override fun markLocalMediaSyncedBefore(synced: Boolean) {
        localMediaRepository.markLocalMediaSyncedBefore(synced)
    }

    override fun hasLocalMediaBeenSyncedBefore(): Boolean =
        localMediaRepository.hasLocalMediaBeenSyncedBefore()

    private suspend fun resetMediaStoreIfNeeded() {
        with (mediaStoreVersionRepository) {
            if (latestMediaStoreVersion != currentMediaStoreVersion) {
                async {
                    clearAll()
                }
                currentMediaStoreVersion = latestMediaStoreVersion
            }
        }
    }

}