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
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.FULLY_SYNCED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.LOCAL_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.LOCAL_UPLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.REMOTE_DOWNLOADING
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus.REMOTE_ONLY
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import kotlinx.parcelize.Parcelize
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_done
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_download
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_off
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_cloud_upload

@Immutable
@Parcelize
data class NewMediaItemModel(
    val md5Sum: Md5Hash = Md5Hash(""),
    val uri: FeedUri = FeedUri.remote(),
    val isVideo: Boolean = false,
    val syncStatus: FeedItemSyncStatus = FULLY_SYNCED,
    val fallbackColor: Int? = null,
    val isFavourite: Boolean = false,
    val ratio: Float = 1f,
) : Parcelable

val FeedItemSyncStatus.hasRemote get() = this in setOf(
    FULLY_SYNCED,
    REMOTE_DOWNLOADING,
    REMOTE_ONLY,
)

val FeedItemSyncStatus.hasLocal get() = this in setOf(
    FULLY_SYNCED,
    LOCAL_UPLOADING,
    LOCAL_ONLY,
)

val FeedItemSyncStatus.icon get() = when (this) {
    LOCAL_ONLY -> drawable.ic_cloud_off
    REMOTE_ONLY -> drawable.ic_cloud
    LOCAL_UPLOADING -> drawable.ic_cloud_upload
    REMOTE_DOWNLOADING -> drawable.ic_cloud_download
    FULLY_SYNCED -> drawable.ic_cloud_done
}