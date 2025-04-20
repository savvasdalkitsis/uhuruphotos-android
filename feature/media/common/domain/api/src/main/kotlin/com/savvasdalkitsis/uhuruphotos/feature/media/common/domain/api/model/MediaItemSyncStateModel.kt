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

import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_done
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_download
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_in_progress
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_off
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_upload
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_downloading
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_fully_synced
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_local_only
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_processing
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_remote_only
import uhuruphotos_android.foundation.strings.api.generated.resources.media_sync_status_uploading

enum class MediaItemSyncStateModel(
    val icon: DrawableResource,
    val lightBoxIcon: Either<DrawableResource, AnimationResource>,
    val lightBoxIconAlpha: Float = 0.7f,
    val contentDescription: StringResource,
    val enabled: Boolean = false,
) {
    LOCAL_ONLY(
        icon = drawable.ic_cloud_off,
        lightBoxIcon = Either.Left(drawable.ic_cloud_upload),
        lightBoxIconAlpha = 1f,
        contentDescription = string.media_sync_status_local_only,
        enabled = true,
    ),
    REMOTE_ONLY(
        icon = drawable.ic_cloud,
        lightBoxIcon = Either.Left(drawable.ic_cloud_download),
        lightBoxIconAlpha = 1f,
        contentDescription = string.media_sync_status_remote_only,
        enabled = true,
    ),
    DOWNLOADING(
        icon = drawable.ic_cloud_download,
        lightBoxIcon = Either.Right(AnimationResource.ic_animated_cloud_download),
        contentDescription = string.media_sync_status_downloading,
    ),
    UPLOADING(
        icon = drawable.ic_cloud_upload,
        lightBoxIcon = Either.Right(AnimationResource.ic_animated_cloud_upload),
        contentDescription = string.media_sync_status_uploading,
    ),
    PROCESSING(
        icon = drawable.ic_cloud_in_progress,
        lightBoxIcon = Either.Left(drawable.ic_cloud_in_progress),
        contentDescription = string.media_sync_status_processing,
    ),
    SYNCED(
        icon = drawable.ic_cloud_done,
        lightBoxIcon = Either.Left(drawable.ic_cloud_done),
        contentDescription = string.media_sync_status_fully_synced,
    );
}
