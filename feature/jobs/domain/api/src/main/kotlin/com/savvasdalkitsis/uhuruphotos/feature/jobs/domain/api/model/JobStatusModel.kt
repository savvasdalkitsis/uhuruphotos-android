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
package com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class JobStatusModel : Parcelable {
    data object IdleModel : JobStatusModel()
    data class InProgressModel(val progress: Int) : JobStatusModel()
    data object BlockedModel : JobStatusModel()
    data object FailedModel : JobStatusModel()
    data object QueuedModel : JobStatusModel()
}
