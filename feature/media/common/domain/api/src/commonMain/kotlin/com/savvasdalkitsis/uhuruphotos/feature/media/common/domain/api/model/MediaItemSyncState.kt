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

import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.IconResource
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.IconResource.Image
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.IconResource.Json
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.files
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class MediaItemSyncState(
    val icon: ImageResource,
    val lightBoxIcon: IconResource,
    val lightBoxIconAlpha: Float = 0.7f,
    val contentDescription: StringResource,
    val enabled: Boolean = false,
) {
    LOCAL_ONLY(
        icon = images.ic_cloud_off,
        lightBoxIcon = Image(images.ic_cloud_upload),
        lightBoxIconAlpha = 1f,
        contentDescription = strings.media_sync_status_local_only,
        enabled = true,
    ),
    REMOTE_ONLY(
        icon = images.ic_cloud,
        lightBoxIcon = Image(images.ic_cloud_download),
        lightBoxIconAlpha = 1f,
        contentDescription = strings.media_sync_status_remote_only,
        enabled = true,
    ),
    DOWNLOADING(
        icon = images.ic_cloud_download,
        lightBoxIcon = Json(files.ic_animated_cloud_download_json),
        contentDescription = strings.media_sync_status_downloading,
    ),
    UPLOADING(
        icon = images.ic_cloud_upload,
        lightBoxIcon = Json(files.ic_animated_cloud_upload_json),
        contentDescription = strings.media_sync_status_uploading,
    ),
    PROCESSING(
        icon = images.ic_cloud_in_progress,
        lightBoxIcon = Image(images.ic_cloud_in_progress),
        contentDescription = strings.media_sync_status_processing,
    ),
    SYNCED(
        icon = images.ic_cloud_done,
        lightBoxIcon = Image(images.ic_cloud_done),
        contentDescription = strings.media_sync_status_fully_synced,
    );
}
