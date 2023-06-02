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
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.*
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.provider.MediaStore.createDeleteRequest
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletion.*
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestLauncher
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class LocalMediaDeletionUseCase @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val localMediaRepository: LocalMediaRepository,
    private val localMediaService: LocalMediaService,
    private val contentResolver: ContentResolver,
    private val activityRequestLauncher: ActivityRequestLauncher,
) : LocalMediaDeletionUseCase {

    private val requiredDeletePermissions = if (SDK_INT >= Q) {
        emptyArray()
    } else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    override suspend fun deleteLocalMediaItems(items: List<LocalMediaDeletionRequest>): LocalMediaItemDeletion =
        when (val result = deleteMediaItems(items)) {
            is Error -> LocalMediaItemDeletion.Error(result.e ?: UnknownError())
            is RequiresPermissions -> LocalMediaItemDeletion.RequiresPermissions(result.deniedPermissions)

            is NeedsSystemApproval -> {
                val requestResult = activityRequestLauncher.performRequest(
                    "deleteLocalMedia:${items.map { it.id }}",
                    result.request,
                ).onSuccess {
                    // deleting again cause on R sometimes the file is not deleted at the same flow
                    // we give permission in
                    localMediaRepository.removeItemsFromDb(*(items.map { it.id }.toLongArray()))
                    deleteMediaItems(items)
                }
                when {
                    requestResult.isSuccess -> LocalMediaItemDeletion.Success
                    else -> LocalMediaItemDeletion.Error(requestResult.exceptionOrNull() ?: UnknownError())
                }
            }
            Success -> LocalMediaItemDeletion.Success
        }


    private fun deleteMediaItems(items: List<LocalMediaDeletionRequest>): InternalLocalMediaItemDeletion =
        when {
            items.isEmpty() -> Success
            SDK_INT == Q -> qDeletion(*items.toTypedArray())
            SDK_INT >= R -> postRDeletion(*items.toTypedArray())
            else -> preQDeletion(*items.toTypedArray())
        }

    private fun preQDeletion(vararg request: LocalMediaDeletionRequest): InternalLocalMediaItemDeletion =
        when (val permissions = checkPermissions(*requiredDeletePermissions)) {
            is LocalPermissions.RequiresPermissions -> RequiresPermissions(
                permissions.deniedPermissions
            )
            LocalPermissions.Granted -> attemptDeletion(*request)
        }

    private fun checkPermissions(vararg permissions: String): LocalPermissions {
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PERMISSION_GRANTED
        }
        return if (missing.isEmpty()) {
            LocalPermissions.Granted
        } else {
            LocalPermissions.RequiresPermissions(missing)
        }
    }

    @RequiresApi(Q)
    private fun qDeletion(vararg request: LocalMediaDeletionRequest): InternalLocalMediaItemDeletion {
        val result = attemptDeletion(*request)
        return if (result !is Error) {
            result
        } else {
            val error = result.e
            if (error !is RecoverableSecurityException) {
                result
            } else {
                NeedsSystemApproval(
                    IntentSenderRequest.Builder(
                        error.userAction.actionIntent
                    ).build()
                )
            }
        }
    }

    @RequiresApi(R)
    private fun postRDeletion(vararg request: LocalMediaDeletionRequest): InternalLocalMediaItemDeletion =
        NeedsSystemApproval(
            IntentSenderRequest.Builder(
                createDeleteRequest(contentResolver,
                    request.map { (id, video) -> localMediaService.createMediaItemUri(id, video) }
                )
            ).setFillInIntent(null)
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                .build()
        )

    private fun attemptDeletion(
        vararg request: LocalMediaDeletionRequest,
    ): InternalLocalMediaItemDeletion {
        val (videos, photos) = request.partition { it.isVideo }
        val photosResult = localMediaRepository.deletePhotos(*(photos.map { it.id }.toLongArray()))
        val videosResult = localMediaRepository.deleteVideos(*(videos.map { it.id }.toLongArray()))
        return when {
            photosResult.isSuccess && videosResult.isSuccess -> Success
            else -> Error(
                photosResult.exceptionOrNull() ?:
                (videosResult.exceptionOrNull() ?: UnknownError())
            )
        }
    }
}