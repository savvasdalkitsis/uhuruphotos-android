package com.savvasdalkitsis.uhuruphotos.person.usecase

import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.albums.usecase.AlbumsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonUseCase @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
) {

    fun getPersonAlbums(id: Int): Flow<List<Album>> =
        albumsUseCase.getPersonAlbums(id)
}
