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

import android.content.Intent

sealed class LocalMediaItemDeletion {

    object Success : LocalMediaItemDeletion()

    data class Error(val e: Throwable) : LocalMediaItemDeletion()
    data class RequiresPermissions(val deniedPermissions: List<String>) : LocalMediaItemDeletion()
    data class RequiresManageFilesAccess(val request: StoragePermissionRequest) : LocalMediaItemDeletion()

}
