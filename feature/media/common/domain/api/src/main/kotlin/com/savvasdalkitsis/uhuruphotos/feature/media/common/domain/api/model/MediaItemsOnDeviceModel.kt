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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

import android.os.Parcelable
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaFolder
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class MediaItemsOnDeviceModel : Parcelable {

    data class FoundModel(
        val primaryFolder: Pair<LocalMediaFolder, List<MediaItemModel>>?,
        val mediaFolders: List<Pair<LocalMediaFolder, List<MediaItemModel>>>,
    ) : MediaItemsOnDeviceModel()

    data class RequiresPermissionsModel(val deniedPermissions: List<String>) : MediaItemsOnDeviceModel()

    data object ErrorModel: MediaItemsOnDeviceModel()
}