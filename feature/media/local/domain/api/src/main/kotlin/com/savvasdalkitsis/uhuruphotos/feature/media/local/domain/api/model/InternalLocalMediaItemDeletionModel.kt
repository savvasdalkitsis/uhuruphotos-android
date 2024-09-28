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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model

import androidx.activity.result.IntentSenderRequest

sealed class InternalLocalMediaItemDeletionModel {

    data object SuccessModel : InternalLocalMediaItemDeletionModel()
    data class ErrorModel(val e: Throwable?) : InternalLocalMediaItemDeletionModel()
    data class RequiresPermissionsModel(val deniedPermissions: List<String>) : InternalLocalMediaItemDeletionModel()
    data class NeedsSystemApprovalModel(val request: IntentSenderRequest) : InternalLocalMediaItemDeletionModel()
}
