/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestRemoteMediaCollections.completeRemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestRemoteMediaCollections.getRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestRemoteMediaCollections.incompleteRemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestRemoteMediaCollections.remoteMediaCollections

const val SERVER_ALBUM_LOCATION = "serverAlbumLocation"

fun collectionId(id: Int) = "collection$id"

fun collection(id: Int) = remoteMediaCollections.copy(id = collectionId(id), date = "2000-01-0$id")

fun incompleteRemoteMediaCollection(id: Int) = incompleteRemoteMediaCollection.copy(
    id = collection(id).id,
    date = collection(id).date
)

fun completeRemoteMediaCollection(id: Int) = completeRemoteMediaCollection.copy(
    id = collection(id).id,
    date = collection(id).date
)

fun collection(album: Int, vararg albums: GetPersonAlbums) = collectionId(album) to albums.map {
    it.copy(id = collectionId(album), albumDate = collection(album).date)
}

fun collection(album: Int, vararg albums: GetRemoteMediaCollections) = collectionId(album) to albums.map {
    it.copy(id = collectionId(album), albumDate = collection(album).date)
}

fun entry(mediaItemSummary: RemoteMediaItemSummary) = getRemoteMediaCollections.copy(photoId = mediaItemSummary.id)

fun GetRemoteMediaCollections.withServerResponseData() = copy(
    dominantColor = "",
    rating = 0,
    aspectRatio = 1f,
    type = "",
    albumLocation = SERVER_ALBUM_LOCATION,
)
