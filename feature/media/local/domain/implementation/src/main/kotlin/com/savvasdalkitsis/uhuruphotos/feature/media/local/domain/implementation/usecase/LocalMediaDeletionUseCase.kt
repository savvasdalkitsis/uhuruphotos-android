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
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Build.VERSION_CODES.R
import android.provider.MediaStore.createDeleteRequest
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.michaelbull.result.combine
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletionModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletionModel.ErrorModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletionModel.NeedsSystemApprovalModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletionModel.RequiresPermissionsModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.InternalLocalMediaItemDeletionModel.SuccessModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaDeletionRequestModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItemDeletion
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalPermissions
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository.LocalMediaRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.request.ActivityRequestLauncher
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simpleOk
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

    override suspend fun deleteLocalMediaItems(items: List<LocalMediaDeletionRequestModel>): LocalMediaItemDeletion =
        when (val result = deleteMediaItems(items)) {
            is ErrorModel -> LocalMediaItemDeletion.Error(result.e ?: UnknownError())
            is RequiresPermissionsModel -> LocalMediaItemDeletion.RequiresPermissions(result.deniedPermissions)

            is NeedsSystemApprovalModel -> approveDeletion(items, result.request)
            SuccessModel -> LocalMediaItemDeletion.Success
        }

    private suspend fun approveDeletion(
        items: List<LocalMediaDeletionRequestModel>,
        request: IntentSenderRequest,
    ): LocalMediaItemDeletion {
        val requestResult = activityRequestLauncher.performRequest("deleteLocalMedia:${items.map { it.id }}", request)
            .onSuccess { completeApprovedDeletion(items) }
        return when {
            requestResult.isOk -> LocalMediaItemDeletion.Success
            else -> LocalMediaItemDeletion.Error(requestResult.error)
        }
    }

    private suspend fun completeApprovedDeletion(items: List<LocalMediaDeletionRequestModel>) {
        localMediaRepository.removeItemsFromDb(*items.ids)
        if (SDK_INT < R) {
            deleteMediaItems(items)
        }
    }

    private suspend fun deleteMediaItems(items: List<LocalMediaDeletionRequestModel>): InternalLocalMediaItemDeletionModel =
        when {
            items.isEmpty() -> SuccessModel
            SDK_INT == Q -> qDeletion(*items.toTypedArray())
            SDK_INT >= R -> postRDeletion(items)
            else -> preQDeletion(*items.toTypedArray())
        }

    private fun preQDeletion(vararg request: LocalMediaDeletionRequestModel): InternalLocalMediaItemDeletionModel =
        when (val permissions = checkPermissions(*requiredDeletePermissions)) {
            is LocalPermissions.RequiresPermissions -> RequiresPermissionsModel(
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
    private fun qDeletion(vararg request: LocalMediaDeletionRequestModel): InternalLocalMediaItemDeletionModel {
        val result = attemptDeletion(*request)
        return if (result !is ErrorModel) {
            result
        } else {
            val error = result.e
            if (error !is RecoverableSecurityException) {
                result
            } else {
                NeedsSystemApprovalModel(
                    IntentSenderRequest.Builder(
                        error.userAction.actionIntent
                    ).build()
                )
            }
        }
    }

    @RequiresApi(R)
    private suspend fun postRDeletion(items: List<LocalMediaDeletionRequestModel>): InternalLocalMediaItemDeletionModel {
        val (present, gone) = items.partition { (id, video) -> localMediaService.exists(id, video) }
        if (gone.isNotEmpty()) {
            localMediaRepository.removeItemsFromDb(*gone.ids)
        }
        return when {
            present.isEmpty() -> SuccessModel
            else -> requestSystemDeletion(present)
        }
    }

    @RequiresApi(R)
    private fun requestSystemDeletion(items: List<LocalMediaDeletionRequestModel>): InternalLocalMediaItemDeletionModel =
        runCatchingWithLog {
            NeedsSystemApprovalModel(
                IntentSenderRequest.Builder(
                    createDeleteRequest(contentResolver,
                        items.map { (id, video) -> localMediaService.createMediaItemUri(id, video) }
                    )
                ).setFillInIntent(null)
                    .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                    .build()
            )
        }.getOrElse { ErrorModel(it) }

    private val List<LocalMediaDeletionRequestModel>.ids get() = map { it.id }.toLongArray()

    private fun attemptDeletion(
        vararg request: LocalMediaDeletionRequestModel,
    ): InternalLocalMediaItemDeletionModel {
        val (videos, photos) = request.partition { it.isVideo }
        val photosResult = deleteIfAny(photos, localMediaRepository::deletePhotos)
        val videosResult = deleteIfAny(videos, localMediaRepository::deleteVideos)
        val result = combine(photosResult, videosResult)
        return when {
            result.isOk -> SuccessModel
            else -> ErrorModel(result.error)
        }
    }

    private fun deleteIfAny(
        items: List<LocalMediaDeletionRequestModel>,
        delete: (LongArray) -> SimpleResult,
    ): SimpleResult = when {
        items.isEmpty() -> simpleOk
        else -> delete(items.map { it.id }.toLongArray())
    }
}
