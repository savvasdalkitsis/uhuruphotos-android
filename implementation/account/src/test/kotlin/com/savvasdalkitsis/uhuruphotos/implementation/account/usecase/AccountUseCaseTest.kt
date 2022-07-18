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
package com.savvasdalkitsis.uhuruphotos.implementation.account.usecase

import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.savvasdalkitsis.uhuruphotos.api.db.Database
import com.savvasdalkitsis.uhuruphotos.api.db.albums.AlbumsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.AutoAlbumPeopleQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.AutoAlbumQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.AutoAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbumQueries
import com.savvasdalkitsis.uhuruphotos.api.db.albums.UserAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.api.db.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.api.db.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetailsQueries
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoSummaryQueries
import com.savvasdalkitsis.uhuruphotos.api.db.photos.TrashQueries
import com.savvasdalkitsis.uhuruphotos.api.db.search.SearchQueries
import com.savvasdalkitsis.uhuruphotos.api.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.api.image.cache.ImageCacheController
import com.savvasdalkitsis.uhuruphotos.api.worker.WorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.video.evictAll
import com.squareup.sqldelight.TransactionWithReturn
import com.squareup.sqldelight.TransactionWithoutReturn
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AccountUseCaseTest {

    private val imageCacheController = mockk<ImageCacheController>(relaxed = true)
    private val videoCache = mockk<CacheDataSource.Factory>(relaxed = true)
    private val workScheduler = mockk<WorkScheduler>(relaxed = true)
    private val db = object: Database {
        override val albumsQueries = mockk<AlbumsQueries>(relaxed = true)
        override val autoAlbumQueries = mockk<AutoAlbumQueries>(relaxed = true)
        override val autoAlbumPeopleQueries = mockk<AutoAlbumPeopleQueries>(relaxed = true)
        override val autoAlbumPhotosQueries = mockk<AutoAlbumPhotosQueries>(relaxed = true)
        override val autoAlbumsQueries = mockk<AutoAlbumsQueries>(relaxed = true)
        override val peopleQueries = mockk<PeopleQueries>(relaxed = true)
        override val personQueries = mockk<PersonQueries>(relaxed = true)
        override val photoDetailsQueries = mockk<PhotoDetailsQueries>(relaxed = true)
        override val photoSummaryQueries = mockk<PhotoSummaryQueries>(relaxed = true)
        override val searchQueries = mockk<SearchQueries>(relaxed = true)
        override val tokenQueries = mockk<TokenQueries>(relaxed = true)
        override val userQueries = mockk<UserQueries>(relaxed = true)
        override val userAlbumQueries = mockk<UserAlbumQueries>(relaxed = true)
        override val userAlbumPhotosQueries = mockk<UserAlbumPhotosQueries>(relaxed = true)
        override val userAlbumsQueries = mockk<UserAlbumsQueries>(relaxed = true)
        override val trashQueries = mockk<TrashQueries>(relaxed = true)

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
        imageCacheController,
        videoCache,
        workScheduler,
    )

    @Test
    fun `cancels all scheduled work when logging out`() = runBlocking {
        underTest.logOut()

        verify { workScheduler.cancelAllScheduledWork() }
    }

    @Test
    fun `clears all database tables when logging out`() = runBlocking {
        underTest.logOut()

        verify {
            with (db) {
                albumsQueries.clearAll()
                autoAlbumQueries.clearAll()
                autoAlbumPeopleQueries.clearAll()
                autoAlbumPhotosQueries.clearAll()
                autoAlbumsQueries.clearAll()
                peopleQueries.clearAll()
                personQueries.clearAll()
                photoDetailsQueries.clearAll()
                photoSummaryQueries.clearAll()
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

        verify { imageCacheController.clear() }
    }

//    @Test
//    fun `clears video cache when logging out`() = runBlocking {
//        underTest.logOut()
//
//        verify { videoCache.evictAll() }
//    }
}