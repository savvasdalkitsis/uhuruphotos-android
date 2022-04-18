package com.savvasdalkitsis.librephotos.account.usecase

import com.savvasdalkitsis.librephotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.librephotos.db.auth.TokenQueries
import com.savvasdalkitsis.librephotos.db.extensions.crud
import com.savvasdalkitsis.librephotos.db.search.SearchQueries
import com.savvasdalkitsis.librephotos.db.user.UserQueries
import com.savvasdalkitsis.librephotos.image.cache.ImageCacheController
import com.savvasdalkitsis.librephotos.worker.WorkScheduler
import javax.inject.Inject

class AccountUseCase @Inject constructor(
    private val userQueries: UserQueries,
    private val albumsQueries: AlbumsQueries,
    private val searchQueries: SearchQueries,
    private val tokenQueries: TokenQueries,
    private val imageCacheController: ImageCacheController,
    private val workScheduler: WorkScheduler,
) {

    suspend fun logOut() {
        crud {
            workScheduler.cancelAllScheduledWork()
            albumsQueries.clearAlbums()
            searchQueries.clearSearchResults()
            userQueries.deleteUser()
            tokenQueries.removeAllTokens()
            imageCacheController.clear()
        }
    }
}