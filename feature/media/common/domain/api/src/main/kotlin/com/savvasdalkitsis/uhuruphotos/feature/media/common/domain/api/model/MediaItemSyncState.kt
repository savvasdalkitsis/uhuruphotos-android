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
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.raw
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

enum class MediaItemSyncState(
    @DrawableRes
    val icon: Int,
    @DrawableRes
    val lightBoxIcon: Int,
    val lightBoxIconAlpha: Float = 0.7f,
    @StringRes
    val contentDescription: Int,
) {
    LOCAL_ONLY(
        icon = drawable.ic_cloud_off,
//        lightBoxIcon = drawable.ic_cloud_off,
        lightBoxIcon = drawable.ic_cloud_upload,
        lightBoxIconAlpha = 1f,
        contentDescription = string.media_sync_status_local_only
    ),
    REMOTE_ONLY(
        icon = drawable.ic_cloud,
        lightBoxIcon = drawable.ic_cloud_download,
        lightBoxIconAlpha = 1f,
        contentDescription = string.media_sync_status_remote_only
    ),
    DOWNLOADING(
        icon = raw.ic_animated_cloud_download,
        lightBoxIcon = raw.ic_animated_cloud_download,
        contentDescription = string.media_sync_status_downloading
    ),
    UPLOADING(
        icon = raw.ic_animated_cloud_upload,
        lightBoxIcon = raw.ic_animated_cloud_upload,
        contentDescription = string.media_sync_status_uploading
    ),
    SYNCED(
        icon = drawable.ic_cloud_done,
        lightBoxIcon = drawable.ic_cloud_done,
        contentDescription = string.media_sync_status_fully_synced
    );
}
