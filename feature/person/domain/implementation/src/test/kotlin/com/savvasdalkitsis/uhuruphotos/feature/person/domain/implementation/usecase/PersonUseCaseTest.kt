/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.usecase

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaCollection
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.TestMedia.mediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.TestGetPersonAlbums.getPersonAlbum
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.repository.PersonRepository
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PersonUseCaseTest {

    private val personRepository = mockk<PersonRepository>(relaxed = true)
    private val mediaUseCase = mockk<MediaUseCase>(relaxed = true)
    private val feedUseCase = mockk<FeedUseCase>(relaxed = true)
    private val underTest = PersonUseCase(
        personRepository,
        mediaUseCase,
        feedUseCase,
    )

    @Test
    fun `observes person albums from repository and updates`() = runTest {
        val element = getPersonAlbum.copy(
            id = "collectionId",
            photoId = "mediaItemId",
        )
        coEvery { personRepository.observePersonAlbums(1) } returns MutableStateFlow(
            Group("collectionId" to listOf(element))
        )

        underTest.observePersonMedia(1).test {
            assertThat(
                awaitItem(), sameBeanAs(
                    listOf(
                        mediaCollection.copy(
                            id = "collectionId",
                            mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("mediaItemId", false, "serverUrl")))
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `refreshes albums when observing people albums if they are missing from repo`() = runBlocking {
        feedUseCase.reportsHavingNoFeed()

        underTest.observePersonMedia(1).collect()

        verify { feedUseCase.refreshFeed(false) }
    }

    @Test
    fun `ignores errors when refreshing albums after observing people albums`() = runBlocking {
        feedUseCase.reportsHavingNoFeed()
        every { feedUseCase.refreshFeed(false) } throws IllegalStateException()

        underTest.observePersonMedia(1).collect()
    }

    @Test
    fun `does not refresh albums when observing people albums if they are not missing from repo`() = runBlocking {
        feedUseCase.reportsHavingFeed()

        underTest.observePersonMedia(1).collect()

        verify { mediaUseCase wasNot Called }
    }


    @Test
    fun `gets person albums from repository`() = runBlocking {
        personRepository.returnsPersonAlbumWithEntries(personId = 1, getPersonAlbum.copy(
            id = "collectionId",
            photoId = "mediaItemId",
        ))

        assertThat(underTest.getPersonMedia(1), sameBeanAs(listOf(mediaCollection.copy(
            id = "collectionId",
            mediaItems = listOf(mediaItem.copy(id = MediaId.Remote("mediaItemId", false, "serverUrl")))
        ))))
    }

    private fun FeedUseCase.reportsHavingNoFeed() {
        coEvery { hasFeed() } returns false
    }

    private fun FeedUseCase.reportsHavingFeed() {
        coEvery { hasFeed() } returns true
    }

    private fun PersonRepository.returnsPersonAlbumWithEntries(personId: Int, vararg albums: GetPersonAlbums) {
        coEvery { getPersonAlbums(personId) } returns Group(mapOf("albumId" to albums.toList()))
    }
}
