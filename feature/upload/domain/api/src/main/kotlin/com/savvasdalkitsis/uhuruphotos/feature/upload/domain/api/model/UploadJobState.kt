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
package com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

data class UploadJobState(
    val state: WorkInfo.State,
    val progressPercent: Float?,
)

sealed class UploadStatus(
    val isFinished: Boolean,
    val displayName: Int,
) {
    data object InQueue : UploadStatus(false, string.queued)
    data class Uploading(val progressPercent: Float): UploadStatus(false, string.uploading)
    data object Processing: UploadStatus(false, string.processing)
    data object Finished: UploadStatus(true, string.succeeded)
    data class Failed(val lastResponse: String?): UploadStatus(true, string.failed)
}