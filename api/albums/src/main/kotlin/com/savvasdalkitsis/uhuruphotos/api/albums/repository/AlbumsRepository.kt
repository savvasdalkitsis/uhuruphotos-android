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

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.GetUserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.GetTrash
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import kotlinx.coroutines.flow.Flow

interface AlbumsRepository {

    suspend fun hasAlbums(): Boolean
    fun observeAlbumsByDate() : Flow<Group<String, GetRemoteMediaCollections>>
    suspend fun getAlbumsByDate() : Group<String, GetRemoteMediaCollections>
    suspend fun refreshAlbums(shallow: Boolean, onProgressChange: suspend (Int) -> Unit): Result<Unit>
    suspend fun refreshAlbum(albumId: String)

    fun observePersonAlbums(personId: Int) : Flow<Group<String, GetPersonAlbums>>
    suspend fun getPersonAlbums(personId: Int) : Group<String, GetPersonAlbums>

    fun observeAutoAlbums(): Flow<List<AutoAlbums>>
    suspend fun getAutoAlbums(): List<AutoAlbums>
    fun observeAutoAlbum(albumId: Int): Flow<List<GetAutoAlbum>>
    suspend fun getAutoAlbum(albumId: Int): Group<String, GetAutoAlbum>
    fun observeAutoAlbumPeople(albumId: Int): Flow<List<GetPeopleForAutoAlbum>>
    suspend fun refreshAutoAlbums(): Result<Unit>
    suspend fun refreshAutoAlbum(albumId: Int): Result<Unit>

    fun observeUserAlbums(): Flow<List<UserAlbums>>
    suspend fun getUserAlbums(): List<UserAlbums>
    suspend fun getUserAlbum(albumId: Int): Group<String, GetUserAlbum>
    fun observeUserAlbum(albumId: Int): Flow<Group<String, GetUserAlbum>>
    suspend fun refreshUserAlbums(): Result<Unit>
    suspend fun refreshUserAlbum(albumId: Int): Result<Unit>

    fun observeTrash(): Flow<Group<String, GetTrash>>
    suspend fun hasTrash(): Boolean
    suspend fun getTrash(): Group<String, GetTrash>
    suspend fun refreshTrash(): Result<Unit>
}