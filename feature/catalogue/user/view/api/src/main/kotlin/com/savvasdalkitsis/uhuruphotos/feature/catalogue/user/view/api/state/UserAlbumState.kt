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

import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.VitrineState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.toTitleOr

data class UserAlbumState(
    val id: Int,
    val cover: VitrineState,
    val title: Title,
    val photoCount: Int?,
)

context(RemoteMediaUseCase)
fun UserAlbums.toUserAlbumState() = UserAlbumState(
    id = id,
    cover = VitrineState(
        cel1 = celState(
            coverPhoto1Hash,
            coverPhoto1IsVideo
        ),
        cel2 = celState(
            coverPhoto2Hash,
            coverPhoto2IsVideo
        ),
        cel3 = celState(
            coverPhoto3Hash,
            coverPhoto3IsVideo
        ),
        cel4 = celState(
            coverPhoto4Hash,
            coverPhoto4IsVideo
        ),
    ),
    title = title.toTitleOr(string.missing_album_title),
    photoCount = photoCount,
)

context(RemoteMediaUseCase)
private fun celState(imageHash: String?, coverIsVideo: Boolean?): CelState? =
    imageHash?.let {
        MediaItem(
            id = MediaId.Remote(it),
            mediaHash = it,
            thumbnailUri = it.toThumbnailUrlFromId(),
            fullResUri = it.toFullSizeUrlFromId(coverIsVideo ?: false),
            displayDayDate = null,
            ratio = 1f,
            isVideo = coverIsVideo ?: false,
        ).toCel()
    }