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
package com.savvasdalkitsis.uhuruphotos.feature.account.domain.implementation.usecase

import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrashQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.cache.ImageCacheUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import com.squareup.sqldelight.TransactionWithReturn
import com.squareup.sqldelight.TransactionWithoutReturn
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AccountUseCaseTest {

    private val imageCacheUseCase = mockk<ImageCacheUseCase>(relaxed = true)
    private val videoCache = mockk<CacheDataSource.Factory>(relaxed = true)
    private val workScheduleUseCase = mockk<WorkScheduleUseCase>(relaxed = true)
    private val db = object: Database {
        override val remoteMediaCollectionsQueries= mockk<RemoteMediaCollectionsQueries>(relaxed = true)
        override val autoAlbumQueries = mockk<AutoAlbumQueries>(relaxed = true)
        override val autoAlbumPeopleQueries = mockk<AutoAlbumPeopleQueries>(relaxed = true)
        override val autoAlbumPhotosQueries = mockk<AutoAlbumPhotosQueries>(relaxed = true)
        override val autoAlbumsQueries = mockk<AutoAlbumsQueries>(relaxed = true)
        override val localMediaItemDetailsQueries = mockk<LocalMediaItemDetailsQueries>(relaxed = true)
        override val peopleQueries = mockk<PeopleQueries>(relaxed = true)
        override val personQueries = mockk<PersonQueries>(relaxed = true)
        override val remoteMediaItemDetailsQueries = mockk<RemoteMediaItemDetailsQueries>(relaxed = true)
        override val remoteMediaItemSummaryQueries = mockk<RemoteMediaItemSummaryQueries>(relaxed = true)
        override val remoteMediaTrashQueries = mockk<RemoteMediaTrashQueries>(relaxed = true)
        override val searchQueries = mockk<SearchQueries>(relaxed = true)
        override val tokenQueries = mockk<TokenQueries>(relaxed = true)
        override val userQueries = mockk<UserQueries>(relaxed = true)
        override val userAlbumQueries = mockk<UserAlbumQueries>(relaxed = true)
        override val userAlbumPhotosQueries = mockk<UserAlbumPhotosQueries>(relaxed = true)
        override val userAlbumsQueries = mockk<UserAlbumsQueries>(relaxed = true)
        override val downloadingMediaItemsQueries = mockk<DownloadingMediaItemsQueries>(relaxed = true)

        override fun transaction(noEnclosing: Boolean, body: TransactionWithoutReturn.() -> Unit) {}
        override fun <R> transactionWithResult(
            noEnclosing: Boolean,
            bodyWithReturn: TransactionWithReturn<R>.() -> R
        ): R {
            throw IllegalStateException("Not yet implemented")
        }

    }

    private val underTest = AccountUseCase(
        db,
        imageCacheUseCase,
        videoCache,
        workScheduleUseCase,
    )

    @Test
    fun `cancels all scheduled work when logging out`() = runBlocking {
        underTest.logOut()

        verify { workScheduleUseCase.cancelAllScheduledWork() }
    }

    @Test
    fun `clears all database tables when logging out`() = runBlocking {
        underTest.logOut()

        verify {
            with (db) {
                remoteMediaCollectionsQueries.clearAll()
                autoAlbumQueries.clearAll()
                autoAlbumPeopleQueries.clearAll()
                autoAlbumPhotosQueries.clearAll()
                autoAlbumsQueries.clearAll()
                peopleQueries.clearAll()
                personQueries.clearAll()
                remoteMediaItemDetailsQueries.clearAll()
                remoteMediaItemSummaryQueries.clearAll()
                remoteMediaTrashQueries.clear()
                searchQueries.clearAll()
                tokenQueries.clearAll()
                userQueries.clearAll()
                userAlbumQueries.clearAll()
                userAlbumPhotosQueries.clearAll()
                userAlbumsQueries.clearAll()
            }
        }
    }

    @Test
    fun `clears image cache when logging out`() = runBlocking {
        underTest.logOut()

        verify { imageCacheUseCase.clearAll() }
    }

//    @Test
//    fun `clears video cache when logging out`() = runBlocking {
//        underTest.logOut()
//
//        verify { videoCache.evictAll() }
//    }
}