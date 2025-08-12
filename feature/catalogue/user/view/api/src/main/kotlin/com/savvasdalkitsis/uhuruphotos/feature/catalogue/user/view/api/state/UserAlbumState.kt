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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state

import android.os.Parcelable
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedItemSyncStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.FeedUri
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.NewMediaItemModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.NewCelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.toTitleOr
import kotlinx.parcelize.Parcelize
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.missing_album_title

@Parcelize
data class UserAlbumState(
    val id: Int,
    val cover: VitrineState,
    val title: Title,
    val photoCount: Int?,
    val visible: Boolean = true,
) : Parcelable

fun UserAlbums.toUserAlbumState() = UserAlbumState(
    id = id,
    cover = VitrineState(
        cel1 = celState(
            coverPhoto1Hash,
            coverPhoto1IsVideo,
        ),
        cel2 = celState(
            coverPhoto2Hash,
            coverPhoto2IsVideo,
        ),
        cel3 = celState(
            coverPhoto3Hash,
            coverPhoto3IsVideo,
        ),
        cel4 = celState(
            coverPhoto4Hash,
            coverPhoto4IsVideo,
        ),
    ),
    title = title.toTitleOr(string.missing_album_title),
    photoCount = photoCount,
)

private fun celState(
    imageHash: String?,
    coverIsVideo: Boolean?,
): NewCelState? =
    imageHash?.let { hash ->
        NewCelState(
            mediaItem = NewMediaItemModel(
                Md5Hash(hash),
                uri = FeedUri.remote(),
                isVideo = coverIsVideo == true,
                syncStatus = FeedItemSyncStatus.FULLY_SYNCED,
                fallbackColor = null,
                isFavourite = false,
                ratio = 1f,
            ),
        )
    }