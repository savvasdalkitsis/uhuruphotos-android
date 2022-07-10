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
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetTrash
import com.savvasdalkitsis.uhuruphotos.api.db.albums.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbums
import com.savvasdalkitsis.uhuruphotos.api.group.model.Group
import kotlinx.coroutines.flow.Flow

interface AlbumsRepository {

    suspend fun hasAlbums(): Boolean
    fun observeAlbumsByDate() : Flow<Group<String, GetAlbums>>
    suspend fun getAlbumsByDate() : Group<String, GetAlbums>
    fun observePersonAlbums(personId: Int) : Flow<Group<String, GetPersonAlbums>>
    suspend fun getPersonAlbums(personId: Int) : Group<String, GetPersonAlbums>
    fun observeAutoAlbums(): Flow<List<AutoAlbums>>
    suspend fun getAutoAlbums(): List<AutoAlbums>
    fun observeUserAlbums(): Flow<List<UserAlbums>>
    suspend fun getUserAlbums(): List<UserAlbums>
    fun observeAutoAlbum(albumId: Int): Flow<List<GetAutoAlbum>>
    suspend fun getAutoAlbum(albumId: Int): Group<String, GetAutoAlbum>
    suspend fun getUserAlbum(albumId: Int): Group<String, GetUserAlbum>
    fun observeAutoAlbumPeople(albumId: Int): Flow<List<GetPeopleForAutoAlbum>>
    fun observeUserAlbum(albumId: Int): Flow<List<GetUserAlbum>>
    fun observeTrash(): Flow<Group<String, GetTrash>>
    suspend fun hasTrash(): Boolean
    suspend fun getTrash(): Group<String, GetTrash>
    suspend fun refreshAutoAlbums()
    suspend fun refreshUserAlbums()
    suspend fun refreshAutoAlbum(albumId: Int)
    suspend fun refreshUserAlbum(albumId: Int)
    suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit)
    suspend fun refreshAlbum(albumId: String)
    suspend fun refreshTrash()
}