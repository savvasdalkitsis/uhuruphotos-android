/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.usecase

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJob
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadJobState
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadStatus
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.model.Uploads
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import io.mockk.every
import kotlinx.coroutines.flow.flowOf

fun UploadsUseCase.defaults() = apply {
    hasNoUploadsInProgress()
    hasNoProcessingInProgress()
}

fun UploadsUseCase.hasNoUploadsInProgress() {
    hasUploadsInProgress()
}

fun UploadsUseCase.hasUploadsInProgress(vararg ids: Long) {
    every { observeUploadsInFlight() }.returns(flowOf(Uploads(ids.map { UploadJob(it, null, "", UploadJobState(WorkInfo.State.RUNNING, 0f), UploadStatus.InQueue) })))
}

fun UploadsUseCase.hasNoProcessingInProgress() {
    hasProcessingInProgress()
}

fun UploadsUseCase.hasProcessingInProgress(vararg ids: Long) {
    every { observeUploadsInFlight() }.returns(flowOf(Uploads(ids.map { UploadJob(it, null, "", UploadJobState(WorkInfo.State.RUNNING, 0f), UploadStatus.Processing) })))
}
