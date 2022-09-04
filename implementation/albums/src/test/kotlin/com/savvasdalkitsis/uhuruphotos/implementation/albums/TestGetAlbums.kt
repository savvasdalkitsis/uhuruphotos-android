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
package com.savvasdalkitsis.uhuruphotos.implementation.albums

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestAlbums.albums
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestAlbums.completeAlbum
import com.savvasdalkitsis.uhuruphotos.implementation.albums.TestAlbums.incompleteAlbum

const val SERVER_ALBUM_LOCATION = "serverAlbumLocation"

fun albumId(id: Int) = "album$id"

fun album(id: Int) = albums.copy(id = albumId(id), date = "2000-01-0$id")

fun incompleteAlbum(id: Int) = incompleteAlbum.copy(
    id = album(id).id,
    date = album(id).date
)

fun completeAlbum(id: Int) = completeAlbum.copy(
    id = album(id).id,
    date = album(id).date
)

fun album(album: Int, vararg albums: GetPersonAlbums) = albumId(album) to albums.map {
    it.copy(id = albumId(album), albumDate = album(album).date)
}

fun album(album: Int, vararg albums: GetAlbums) = albumId(album) to albums.map {
    it.copy(id = albumId(album), albumDate = album(album).date)
}

fun entry(photoSummary: RemoteMediaItemSummary) = TestGetAlbums.getRemoteMediaCollections.copy(photoId = photoSummary.id)

fun entry(personId: Int, photoSummary: RemoteMediaItemSummary) =
    TestGetAlbums.getPersonAlbum.copy(personId = personId, photoId = photoSummary.id)

fun GetPersonAlbums.withServerResponseData() = copy(
    dominantColor = "",
    rating = 0,
    aspectRatio = 1f,
    type = "",
    albumLocation = SERVER_ALBUM_LOCATION,
)

fun GetAlbums.withServerResponseData() = copy(
    dominantColor = "",
    rating = 0,
    aspectRatio = 1f,
    type = "",
    albumLocation = SERVER_ALBUM_LOCATION,
)
