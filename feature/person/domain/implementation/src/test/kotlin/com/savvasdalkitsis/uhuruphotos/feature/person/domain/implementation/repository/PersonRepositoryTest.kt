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
package com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.TestDatabase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.GetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.person.PersonQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.TestGetPersonAlbums
import com.savvasdalkitsis.uhuruphotos.feature.person.domain.implementation.service.PersonService
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import com.shazam.shazamcrest.MatcherAssert.assertThat
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import io.mockk.mockk

class PersonRepositoryTest {

    private val db = TestDatabase.getDb()
    private val personQueries = mockk<PersonQueries>(relaxed = true)
    private val personService = mockk<PersonService>(relaxed = true)
    private val remoteMediaUseCase = mockk<RemoteMediaUseCase>(relaxed = true)

    private val underTest = PersonRepository(
        personQueries,
        personService,
        db.remoteMediaCollectionsQueries,
        remoteMediaUseCase,
    )

    // Commenting these out until the android gradle plugin supports kotlin test
    // fixtures so I can reuse the TestRemoteMediaCollections methods here

//    @Test
//    fun `gets people albums`() = runBlocking {
//        given(collection(1))
//        given(
//            photo(1, inAlbum = 1),
//            photo(2, inAlbum = 1),
//            photo(3, inAlbum = 1),
//        )
//        given(person = 1).hasPhotos(1, 3)
//
//        underTest.getPersonAlbums(1).assertSameAs(
//            album(1,
//                entry(personId = 1, photo(3)),
//                entry(personId = 1, photo(1)),
//            ),
//        )
//    }
//
//    @Test
//    fun `observes people albums and updates`() = runBlocking {
//        given(album(1))
//        given(
//            photo(1, inAlbum = 1),
//            photo(2, inAlbum = 1),
//            photo(3, inAlbum = 1),
//        )
//        given(person = 1).hasPhotos(1, 3)
//
//        underTest.observePersonAlbums(1).test {
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(3)),
//                    entry(personId = 1, photo(1)),
//                )
//            )
//
//            insert(personId = 1, photoId(2))
//
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(3)),
//                    entry(personId = 1, photo(2)),
//                    entry(personId = 1, photo(1)),
//                )
//            )
//        }
//    }
//
//    @Test
//    fun `refreshes people albums when starting to observe`() = runBlocking {
//        given(album(1))
//        given(photo(1, inAlbum = 1))
//        given(person = 1).hasPhotos(1)
//
//        val albumsResponse = albumsService.willRespondForPersonWith(personId = 1,
//            incompleteAlbum(1).copy(location = SERVER_ALBUM_LOCATION),
//            incompleteAlbum(2).copy(location = SERVER_ALBUM_LOCATION),
//        )
//        val album1Response = albumsService.willRespondForPersonAlbum(personId = 1, albumId = 1,
//            completeAlbum(1).copy(
//                items = listOf(
//                    photoSummaryItem(1),
//                    photoSummaryItem(2),
//                )
//            )
//        )
//        val album2Response = albumsService.willRespondForPersonAlbum(personId = 1, albumId = 2,
//            completeAlbum(2).copy(
//                items = listOf(
//                    photoSummaryItem(3),
//                    photoSummaryItem(4),
//                )
//            )
//        )
//
//        underTest.observePersonAlbums(1).test {
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(1)),
//                )
//            )
//
//            albumsResponse.completes()
//
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(1)).copy(albumLocation = SERVER_ALBUM_LOCATION),
//                )
//            )
//
//            album1Response.completes()
//
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(1)).withServerResponseData(),
//                )
//            )
//
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(2)).withServerResponseData(),
//                    entry(personId = 1, photo(1)).withServerResponseData(),
//                )
//            )
//
//            album2Response.completes()
//
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(2)).withServerResponseData(),
//                    entry(personId = 1, photo(1)).withServerResponseData(),
//                ),
//                album(2,
//                    entry(personId = 1, photo(3)).withServerResponseData(),
//                ),
//            )
//
//            awaitItem().assertSameAs(
//                album(1,
//                    entry(personId = 1, photo(2)).withServerResponseData(),
//                    entry(personId = 1, photo(1)).withServerResponseData(),
//                ),
//                album(2,
//                    entry(personId = 1, photo(4)).withServerResponseData(),
//                    entry(personId = 1, photo(3)).withServerResponseData(),
//                ),
//            )
//        }
//    }

    private fun given(vararg albums: RemoteMediaCollections) = insert(*albums)

    private fun insert(vararg albums: RemoteMediaCollections) = albums.forEach {
        db.remoteMediaCollectionsQueries.insert(it)
    }

    private fun given(vararg mediaItemSummaries: DbRemoteMediaItemSummary) = insert(*mediaItemSummaries)

    private fun insert(vararg photoSummaries: DbRemoteMediaItemSummary) =
        photoSummaries.forEach { db.remoteMediaItemSummaryQueries.insert(it) }

    @Suppress("SameParameterValue")
    private fun given(person: Int) = PersonContinuation(person)

    private inner class PersonContinuation(val personId: Int) {
        fun hasPhotos(vararg ids: Int) {
            ids.forEach {
//                insert(personId, photoId(it))
            }
        }
    }

    private fun insert(personId: Int, photoId: String) {
        db.personQueries.insert(id = null, personId = personId, photoId = photoId )
    }

    private fun <T> Group<String, T>.assertSameAs(vararg pairs: Pair<String, List<T>>) =
        assertThat(this, sameBeanAs(Group(mapOf(*pairs))))


    val SERVER_ALBUM_LOCATION = "serverAlbumLocation"

    fun entry(personId: Int, photoSummary: RemoteMediaItemSummary) =
        TestGetPersonAlbums.getPersonAlbum.copy(personId = personId, photoId = photoSummary.id)

    fun GetPersonAlbums.withServerResponseData() = copy(
        dominantColor = "",
        rating = 0,
        aspectRatio = 1f,
        type = "",
        albumLocation = SERVER_ALBUM_LOCATION,
    )
}