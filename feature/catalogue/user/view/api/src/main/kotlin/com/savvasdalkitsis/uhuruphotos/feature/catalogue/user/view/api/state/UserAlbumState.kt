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

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.toTitleOr
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.missing_album_title

data class UserAlbumState(
    val id: Int,
    val cover: VitrineState,
    val title: Title,
    val photoCount: Int?,
    val visible: Boolean = true,
)

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
): CelState? =
    imageHash?.let {
        val isVideo = coverIsVideo ?: false
        MediaItemInstanceModel(
            id = MediaIdModel.RemoteIdModel(it, isVideo),
            mediaHash = MediaItemHashModel.fromRemoteMediaHash(it, 0),
            displayDayDate = null,
            ratio = 1f,
        ).toCel()
    }