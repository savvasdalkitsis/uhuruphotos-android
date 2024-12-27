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
package com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.implementation.usecase

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.processing.domain.api.usecase.ProcessingUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Failed
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.InQueue
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Processing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus.Uploading
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadsUseCase @Inject constructor(
    private val localMediaUseCase: LocalMediaUseCase,
    private val uploadUseCase: UploadUseCase,
    private val processingUseCase: ProcessingUseCase,
    @ApplicationContext private val context: Context,
) : UploadsUseCase {

    override fun observeUploadsInFlight(): Flow<Uploads> =
        combine(
            uploadUseCase.observeUploading(),
            processingUseCase.observeProcessingMedia(),
            uploadUseCase.observeCurrentUpload(),
        ) { uploading, processing, currentUpload ->
            uploading.mapNotNull { itemId ->
                localMediaUseCase.getLocalMediaItem(itemId)?.let { mediaItem ->
                    UploadJob(
                        localItemId = itemId,
                        displayName = mediaItem.displayName,
                        contentUri = mediaItem.contentUri,
                        status = when {
                            currentUpload?.item?.id == itemId ->
                                Uploading(
                                    progressPercent = currentUpload.progressPercent,
                                    progressDisplay = String.format(
                                        locale = context.resources.configuration.locales[0],
                                        format = "%.2f%%",
                                        currentUpload.progressPercent * 100,
                                    )
                                )
                            else -> InQueue
                        }
                    )
                }
            } + processing.jobs.map { item ->
                UploadJob(
                    localItemId = item.localItemId,
                    displayName = item.displayName,
                    contentUri = item.contentUri,
                    status = if (item.hasError) Failed(item.lastResponse) else Processing,
                )
            }
        }.map {
            Uploads(it.sortedWith { a, b ->
                when {
                    a.status is Uploading -> -1
                    b.status is Uploading -> 1
                    a.status is Processing -> -1
                    b.status is Processing -> 1
                    else -> 0
                }
            })
        }
}
