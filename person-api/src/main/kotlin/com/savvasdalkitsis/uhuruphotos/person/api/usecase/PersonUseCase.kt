package com.savvasdalkitsis.uhuruphotos.person.api.usecase

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import kotlinx.coroutines.flow.Flow

interface PersonUseCase {
    fun observePersonAlbums(id: Int): Flow<List<Album>>
    suspend fun getPersonAlbums(id: Int): List<Album>
}