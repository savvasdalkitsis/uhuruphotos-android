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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

enum class MediaItemSyncState(
    @DrawableRes
    val icon: Int,
    @StringRes
    val contentDescription: Int,
) {
    LOCAL_ONLY(drawable.ic_cloud_off, string.media_sync_status_local_only),
    REMOTE_ONLY(drawable.ic_cloud, string.media_sync_status_remote_only),
    SYNCED(drawable.ic_cloud_done, string.media_sync_status_fully_synced);
}
