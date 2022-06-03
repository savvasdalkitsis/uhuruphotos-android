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
package com.savvasdalkitsis.uhuruphotos.api.albums.repository

import com.savvasdalkitsis.uhuruphotos.api.db.albums.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAlbums
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.api.group.model.Group
import kotlinx.coroutines.flow.Flow

interface AlbumsRepository {

    suspend fun hasAlbums(): Boolean
    fun observeAlbumsByDate() : Flow<Group<String, GetAlbums>>
    suspend fun getAlbumsByDate() : Group<String, GetAlbums>
    fun observePersonAlbums(personId: Int) : Flow<Group<String, GetPersonAlbums>>
    suspend fun getPersonAlbums(personId: Int) : Group<String, GetPersonAlbums>
    fun observeAutoAlbums(): Flow<List<AutoAlbums>>
    fun observeAutoAlbum(albumId: Int): Flow<List<GetAutoAlbum>>
    fun observeAutoAlbumPeople(albumId: Int): Flow<List<GetPeopleForAutoAlbum>>
    suspend fun refreshAutoAlbums()
    suspend fun refreshAutoAlbum(albumId: Int)
    suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit)
}