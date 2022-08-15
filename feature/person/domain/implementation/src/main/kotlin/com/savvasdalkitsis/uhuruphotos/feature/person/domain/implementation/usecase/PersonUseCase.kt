package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.albums.usecase.AlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.api.usecase.PersonUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonUseCase @Inject constructor(
    private val albumsUseCase: AlbumsUseCase,
) : PersonUseCase {

    override fun observePersonAlbums(id: Int): Flow<List<Album>> =
        albumsUseCase.observePersonAlbums(id)

    override suspend fun getPersonAlbums(id: Int): List<Album> =
        albumsUseCase.getPersonAlbums(id)
}