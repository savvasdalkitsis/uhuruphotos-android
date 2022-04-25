package com.savvasdalkitsis.uhuruphotos.account.usecase

import com.savvasdalkitsis.uhuruphotos.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.image.cache.ImageCacheController
import com.savvasdalkitsis.uhuruphotos.video.api.VideoCache
import com.savvasdalkitsis.uhuruphotos.worker.WorkScheduler
import okhttp3.Cache
import javax.inject.Inject

class AccountUseCase @Inject constructor(
    private val userQueries: UserQueries,
    private val albumsQueries: AlbumsQueries,
    private val searchQueries: SearchQueries,
    private val tokenQueries: TokenQueries,
    private val imageCacheController: ImageCacheController,
    @VideoCache
    private val videoCache: Cache,
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
            videoCache.evictAll()
        }
    }
}