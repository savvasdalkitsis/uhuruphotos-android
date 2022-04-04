package com.savvasdalkitsis.librephotos.account.usecase

import com.savvasdalkitsis.librephotos.albums.repository.AlbumsRepository
import com.savvasdalkitsis.librephotos.extensions.crud
import com.savvasdalkitsis.librephotos.search.repository.SearchRepository
import com.savvasdalkitsis.librephotos.token.db.TokenQueries
import com.savvasdalkitsis.librephotos.user.repository.UserRepository
import javax.inject.Inject

class AccountUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val albumsRepository: AlbumsRepository,
    private val searchRepository: SearchRepository,
    private val tokenQueries: TokenQueries,
) {

    suspend fun logOut() {
        albumsRepository.removeAllAlbums()
        searchRepository.removeAllSearchResults()
        userRepository.removeUser()
        crud { tokenQueries.removeAllTokens() }
    }
}