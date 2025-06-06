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

import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.failed
import uhuruphotos_android.foundation.strings.api.generated.resources.processing
import uhuruphotos_android.foundation.strings.api.generated.resources.queued
import uhuruphotos_android.foundation.strings.api.generated.resources.succeeded
import uhuruphotos_android.foundation.strings.api.generated.resources.uploading

sealed class UploadStatus(
    val isFinished: Boolean,
    val displayName: StringResource,
) {
    data object InQueue : UploadStatus(false, string.queued)
    data class Uploading(val progressPercent: Float, val progressDisplay: String): UploadStatus(false, string.uploading)
    data object Processing: UploadStatus(false, string.processing)
    data object Finished: UploadStatus(true, string.succeeded)
    data class Failed(val lastResponse: String?): UploadStatus(true, string.failed)
}