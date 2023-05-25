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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.TestDatabase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.await
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.ProgressUpdate
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.SERVER_ALBUM_LOCATION
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.collection
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.collectionId
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.completeRemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.entry
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.incompleteRemoteMediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.mediaItemId
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.mediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.mediaItemSummaryItem
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.FeedService
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.completes
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.respondsForCollection
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.respondsWith
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.willRespondForCollection
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.service.willRespondWith
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.withServerResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestGetPhotoSummaries.photoSummariesForAlbum
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.TestRemoteMediaCollections.remoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FeedRepositoryTest {

    private val db = TestDatabase.getDb()
    private val remoteMediaUseCase = mockk<RemoteMediaUseCase>(relaxed = true)
    private val feedService = mockk<FeedService>(relaxed = true)
    private val underTest = FeedRepository(
        db.remoteMediaCollectionsQueries,
        db.remoteMediaItemSummaryQueries,
        remoteMediaUseCase,
        feedService,
    )

    @Test
    fun `reports having albums if 1 or more exists`() = runBlocking {
        given(remoteMediaCollections)

        assert(underTest.hasRemoteMediaCollections())
    }

    @Test
    fun `reports not having albums if none exists`() = runBlocking {
        db.remoteMediaCollectionsQueries.clearAll()

        assert(!underTest.hasRemoteMediaCollections())
    }

    @Test
    fun `gets albums by date`() = runBlocking {
        given(collection(1), collection(2))
        given(
            mediaItemSummary(1, inCollection = 1),
            mediaItemSummary(2, inCollection = 1),
            mediaItemSummary(3, inCollection = 2),
        )

        underTest.getRemoteMediaCollectionsByDate().assertSameAs(
            collection(1,
                entry(mediaItemSummary(2)),
                entry(mediaItemSummary(1)),
            ),
            collection(2,
                entry(mediaItemSummary(3)),
            ),
        )
    }

    @Test
    fun `observes albums by date and updates`() = runBlocking {
        given(collection(1), collection(2))
        given(
            mediaItemSummary(1, inCollection = 1),
            mediaItemSummary(2, inCollection = 1),
            mediaItemSummary(3, inCollection = 2),
        )

        underTest.observeRemoteMediaCollectionsByDate().test {
            awaitItem().assertSameAs(
                collection(1,
                    entry(mediaItemSummary(2)),
                    entry(mediaItemSummary(1)),
                ),
                collection(2,
                    entry(mediaItemSummary(3)),
                ),
            )

            insert(mediaItemSummary(4, inCollection = 2))

            awaitItem().assertSameAs(
                collection(1,
                    entry(mediaItemSummary(2)),
                    entry(mediaItemSummary(1)),
                ),
                collection(2,
                    entry(mediaItemSummary(4)),
                    entry(mediaItemSummary(3)),
                ),
            )
        }
    }

    @Test
    fun `refreshes albums on demand`() = runBlocking {
        db.remoteMediaCollectionsQueries.clearAll()
        db.remoteMediaItemSummaryQueries.clearAll()
        feedService.respondsWith(
            incompleteRemoteMediaCollection(1).copy(location = SERVER_ALBUM_LOCATION),
        )
        feedService.respondsForCollection(1,
            completeRemoteMediaCollection(1).copy(
                items = listOf(
                    mediaItemSummaryItem(1),
                    mediaItemSummaryItem(2),
                )
            )
        )

        underTest.refreshRemoteMediaCollections(shallow = false) { _,_ ->}

        val album1 = collection(1)

        assertThat(db.remoteMediaCollectionsQueries.getRemoteMediaCollections(limit = -1).await(), sameBeanAs(listOf(
            entry(mediaItemSummary(2, inCollection = 1))
                .copy(id = album1.id, albumDate = album1.date)
                .withServerResponseData(),
            entry(mediaItemSummary(1, inCollection = 1))
                .copy(id = album1.id, albumDate = album1.date)
                .withServerResponseData(),
        )))

        assertThat(db.remoteMediaItemSummaryQueries.getPhotoSummariesForAlbum(album1.id).await(), sameBeanAs(listOf(
            photoSummariesForAlbum.copy(id = mediaItemId(1)),
            photoSummariesForAlbum.copy(id = mediaItemId(2)),
        )))
    }

    @Test(timeout = 4000)
    fun `reports refresh progress`() = runBlocking {
        val progress = ProgressUpdate()

        val albumsResponse = feedService.willRespondWith(
            incompleteRemoteMediaCollection(1),
            incompleteRemoteMediaCollection(2),
        )
        val album1Response = feedService.willRespondForCollection(1, completeRemoteMediaCollection(1))
        val album2Response = feedService.willRespondForCollection(2, completeRemoteMediaCollection(2))

        CoroutineScope(Dispatchers.Default).launch {
            underTest.refreshRemoteMediaCollections(shallow = false, progress)
        }

        progress.assertReceived(0 to 2)
        albumsResponse.completes()

        album1Response.completes()
        progress.assertReceived(1 to 2)

        album2Response.completes()
        progress.assertReceived(2 to 2)
    }

    @Test(timeout = 4000)
    fun `can perform shallow refresh when asked`() = runBlocking {
        val progress = ProgressUpdate()

        val albumsResponse = feedService.willRespondWith(
            incompleteRemoteMediaCollection(1),
            incompleteRemoteMediaCollection(2),
            incompleteRemoteMediaCollection(3),
            incompleteRemoteMediaCollection(4),
        )
        val album1Response = feedService.willRespondForCollection(1, completeRemoteMediaCollection(1))
        val album2Response = feedService.willRespondForCollection(2, completeRemoteMediaCollection(2))
        val album3Response = feedService.willRespondForCollection(3, completeRemoteMediaCollection(3))
        feedService.respondsForCollection(4, completeRemoteMediaCollection(4))
//        coEvery { settingsUseCase.getFeedDaysToRefresh() } returns 3

        CoroutineScope(Dispatchers.Default).launch {
            underTest.refreshRemoteMediaCollections(shallow = true, progress)
        }

        progress.assertReceived(0 to 3)
        albumsResponse.completes()

        album1Response.completes()
        progress.assertReceived(1 to 3)

        album2Response.completes()
        progress.assertReceived(2 to 3)

        album3Response.completes()
        progress.assertReceived(3 to 3)

        coVerify(exactly = 0) { feedService.getRemoteMediaCollection(collectionId(4), 1) }
    }

    private fun given(vararg remoteMediaCollections: RemoteMediaCollections) =
        insert(*remoteMediaCollections)

    private fun insert(vararg albums: RemoteMediaCollections) = albums.forEach {
        db.remoteMediaCollectionsQueries.insert(it)
    }

    private fun given(vararg photoSummaries: DbRemoteMediaItemSummary) = insert(*photoSummaries)

    private fun insert(vararg photoSummaries: DbRemoteMediaItemSummary) =
        photoSummaries.forEach { db.remoteMediaItemSummaryQueries.insert(it) }

    private fun <T> Group<String, T>.assertSameAs(vararg pairs: Pair<String, List<T>>) =
        assertThat(this, sameBeanAs(Group(mapOf(*pairs))))

}