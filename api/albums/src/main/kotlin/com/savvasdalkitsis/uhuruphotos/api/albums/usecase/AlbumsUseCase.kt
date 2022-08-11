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
package com.savvasdalkitsis.uhuruphotos.api.albums.usecase

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumsUseCase {

    fun observePersonAlbums(personId: Int): Flow<List<Album>>
    fun observeAlbums(): Flow<List<Album>>
    fun observeTrash(): Flow<List<Album>>
    suspend fun getPersonAlbums(personId: Int): List<Album>
    suspend fun getAlbums(): List<Album>
    suspend fun getTrash(): List<Album>
    suspend fun hasTrash(): Boolean
    suspend fun getAutoAlbum(albumId: Int): List<Album>
    suspend fun getUserAlbum(albumId: Int): List<Album>
    fun startRefreshAlbumsWork(shallow: Boolean)
    suspend fun refreshAlbum(albumId: String)
    suspend fun refreshTrash(): Result<Unit>
}