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
package com.savvasdalkitsis.uhuruphotos.albums

import com.savvasdalkitsis.uhuruphotos.api.albums.repository.AlbumsRepository
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.group.model.Group
import io.mockk.coEvery

fun AlbumsRepository.reportsHavingNoAlbums() {
    coEvery { hasAlbums() } returns false
}

fun AlbumsRepository.reportsHavingAlbums() {
    coEvery { hasAlbums() } returns true
}

fun AlbumsRepository.returnsAlbums(vararg albums: Pair<String, List<GetAlbums>>) {
    coEvery { getAlbumsByDate() } returns Group(albums.toMap())
}

fun AlbumsRepository.returnsAlbumWithEntries(vararg albums: GetAlbums) {
    coEvery { getAlbumsByDate() } returns Group(mapOf("albumId" to albums.toList()))
}

fun AlbumsRepository.returnsPersonAlbumWithEntries(personId: Int, vararg albums: GetPersonAlbums) {
    coEvery { getPersonAlbums(personId) } returns Group(mapOf("albumId" to albums.toList()))
}