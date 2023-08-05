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

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.usecase.SiteUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability.CanUpload
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability.CannotUpload
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadCapability.UnableToCheck
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.foundation.upload.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.repository.UploadRepository
import com.savvasdalkitsis.uhuruphotos.foundation.upload.implementation.service.UploadService
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadUseCase @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
    private val siteUseCase: SiteUseCase,
    private val uploadRepository: UploadRepository,
    private val uploadService: UploadService,
) : UploadUseCase {

    override suspend fun canUpload(): UploadCapability = siteUseCase.getSiteOptions()
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

    override suspend fun scheduleUpload(vararg items: UploadItem) {
        uploadRepository.setUploading(*(items.map { it.id }.toLongArray()))
        for (item in items) {
            localMediaUseCase.getLocalMediaItem(item.id)?.let { mediaItem ->
                val exists = uploadService.exists(mediaItem.md5)
                when {
                    exists is Ok && !exists.value -> {
                        when (val uploadId = uploadService.upload(mediaItem.contentUri)) {
                            null -> uploadRepository.setNotUploading(mediaItem.id)
                            else -> uploadRepository.associateUploadIdWithMedia(uploadId, mediaItem.id)
                        }
                    }
                    else -> uploadRepository.setNotUploading(mediaItem.id)
                }
            }
        }
    }

    override fun observeUploading(): Flow<Set<Long>> = uploadRepository.observeUploading()

}