package com.savvasdalkitsis.uhuruphotos.albums.api.usecase

import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import kotlinx.coroutines.flow.Flow

interface AlbumsUseCase {

    fun getPersonAlbums(personId: Int): Flow<List<Album>>
    fun observeAlbums(): Flow<List<Album>>
    suspend fun getAlbums(): List<Album>
    fun startRefreshAlbumsWork(shallow: Boolean)
}