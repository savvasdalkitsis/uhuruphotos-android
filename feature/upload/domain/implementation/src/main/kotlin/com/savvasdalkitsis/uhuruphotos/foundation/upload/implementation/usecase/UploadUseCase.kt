/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.usecase

import androidx.work.NetworkType
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload.ProcessingMediaItems
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.toMediaItemHash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.usecase.SiteUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.CanUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.CannotUpload
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.NotSetUpWithAServer
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadCapability.UnableToCheck
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.get
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.model.uploadId
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.UploadService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody.Part.Companion.createFormData
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadUseCase @Inject constructor(
    private val siteUseCase: SiteUseCase,
    private val userUseCase: UserUseCase,
    private val uploadRepository: UploadRepository,
    private val uploadService: UploadService,
    private val localMediaUseCase: LocalMediaUseCase,
    private val remoteMediaUseCase: RemoteMediaUseCase,
    private val uploadWorkScheduler: UploadWorkScheduler,
    private val chunkedUploader: ChunkedUploader,
    private val settingsUseCase: SettingsUseCase,
    private val welcomeUseCase: WelcomeUseCase,
) : UploadUseCase {

    override suspend fun canUpload(): UploadCapability =
        welcomeUseCase.get(
            withoutRemoteAccess = { NotSetUpWithAServer },
            withRemoteAccess = {
                siteUseCase.getSiteOptions()
                    .map {
                        when {
                            it.allowUpload -> CanUpload
                            else -> CannotUpload
                        }
                    }
                    .getOrElse {
                        log(it)
                        UnableToCheck
                    }
            },
        )


    override suspend fun scheduleUpload(
        force: Boolean,
        networkType: NetworkType,
        requiresCharging: Boolean,
        vararg items: UploadItem
    ) {
        uploadRepository.setUploading(*items)
        for (item in items) {
            uploadWorkScheduler.scheduleUpload(force, item, networkType, requiresCharging)
        }
    }

    override suspend fun scheduleUpload(
        force: Boolean,
        vararg items: UploadItem,
    ) {
        val networkRequirements = settingsUseCase.getCloudSyncNetworkRequirements()
        val requiresCharging = settingsUseCase.getCloudSyncRequiresCharging()
        scheduleUpload(force, networkRequirements, requiresCharging, *items)
    }

    override fun markAsNotUploading(vararg mediaIds: Long) {
        uploadRepository.setNotUploading(*mediaIds)
    }

    override fun markAsNotProcessing(mediaId: Long) {
        uploadRepository.setNotProcessing(mediaId)
    }

    override fun saveErrorForProcessingItem(itemId: Long, error: Throwable) {
        uploadRepository.setProcessingError(itemId, error.stackTraceToString())
    }

    override fun saveLastResponseForProcessingItem(itemId: Long, response: String) {
        uploadRepository.setLastResponseForProcessing(itemId, response)
    }

    override fun observeUploading(): Flow<Set<Long>> = uploadRepository.observeUploading()

    override fun observeProcessing(): Flow<Set<ProcessingMediaItems>> = uploadRepository.observeProcessing()

    override suspend fun upload(
        item: UploadItem,
        force: Boolean,
        progress: suspend (current: Long, total: Long) -> Unit,
    ): SimpleResult = coroutineBinding {
        val mediaItem = localMediaItem(item).bind()
        val user = userUseCase.getRemoteUserOrRefresh().bind()

        if (force || !exists(mediaItem.md5, user).bind()) {
            if (force || !uploadRepository.isCompleted(item.id)) {
                chunkedUploader.uploadInChunks(item, mediaItem.size, user, progress).bind()
            }

            completeUpload(item, mediaItem, user).bind()
        }

        uploadRepository.setNotUploading(item.id)
        uploadRepository.setProcessing(item.id)
        uploadWorkScheduler.schedulePostUploadProcessing(mediaItem.md5.toMediaItemHash(user.id), item.id)
    }

    private suspend fun exists(md5: Md5Hash, user: User): Result<Boolean, Throwable> =
        remoteMediaUseCase.exists(md5.toMediaItemHash(user.id))

    private suspend fun completeUpload(
        item: UploadItem,
        mediaItem: LocalMediaItem,
        user: User
    ): SimpleResult = runCatchingWithLog {
        uploadRepository.setCompleted(item.id)
        val filename = mediaItem.displayName
            ?: throw IllegalArgumentException("No name associated with file ${item.id}")
        val uploadId = with(uploadRepository) {
            item.uploadId()
        }.ifEmpty {
            throw IllegalStateException("Upload id not found for item ${item.id}")
        }
        uploadService.completeUpload(
            uploadId = createFormData("upload_id", uploadId),
            user = createFormData("user", user.id.toString()),
            md5 = createFormData("md5", mediaItem.md5.value),
            filename = createFormData("filename", filename),
        )
    }

    private suspend fun localMediaItem(item: UploadItem): Result<LocalMediaItem, Throwable> =
        localMediaUseCase.getLocalMediaItem(item.id)?.let { Ok(it) }
            ?: Err(IllegalArgumentException("Could not find associated local media with id: ${item.id}"))
}
