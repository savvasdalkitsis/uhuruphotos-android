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

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobState
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Completing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Initializing
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Synchronising
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobType.Uploading
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.work.UploadWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model.isFailed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class UploadsUseCase @Inject constructor(
    private val uploadWorkScheduler: UploadWorkScheduler,
    private val localMediaUseCase: LocalMediaUseCase,
    private val uploadUseCase: UploadUseCase,
) : UploadsUseCase {
    override fun observeUploadsInFlight(): Flow<Uploads> = with(uploadWorkScheduler) {
        monitorUploadJobs().map { info ->
            info.mapNotNull { it }
                .groupBy { mediaItemIdFrom(it) }
                .mapNotNull { (itemId, info) ->
                    itemId?.let {
                        localMediaUseCase.getLocalMediaItem(itemId)?.let { mediaItem ->
                            val latestJobState =
                                info.findType(Synchronising).asState(Synchronising) ?:
                                info.findType(Completing).asState(Completing) ?:
                                info.findType(Uploading).asState(Uploading) ?:
                                info.findType(Initializing).asState(Initializing)
                            latestJobState?.let {
                                UploadJob(
                                    localItemId = itemId,
                                    displayName = mediaItem.displayName,
                                    thumbnailUrl = mediaItem.contentUri,
                                    latestJobState = latestJobState,
                                )
                            }
                        }
                    }
                }
                .sortedBy { it.displayName }
        }.map { jobs -> Uploads(jobs)
            .also { uploads ->
                val finishedJobs = uploads.jobs.filter { job ->
                    job.latestJobState.jobType.isLast && job.latestJobState.state.isFinished
                }
                uploadUseCase.markAsNotUploading(*(finishedJobs.map { it.localItemId }.toLongArray()))
            }
        }
    }

    private fun List<WorkInfo.State>.asState(type: UploadJobType): UploadJobState? =
        sortedWith { a, b ->
            when {
                !a.isFinished -> -1
                !b.isFinished -> 1
                a.isFailed -> -1
                b.isFailed -> 1
                else -> 0
            }
        }.firstOrNull()?.let { UploadJobState(it, type) }

}