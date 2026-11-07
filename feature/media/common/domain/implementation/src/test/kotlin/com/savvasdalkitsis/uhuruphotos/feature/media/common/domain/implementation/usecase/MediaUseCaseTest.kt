/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaItemSummary
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaCollectionSourceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.Md5Hash
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.RemoteUserModel
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateParser
import com.savvasdalkitsis.uhuruphotos.foundation.group.api.model.Group
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaUseCaseTest {

    private val md5 = "9e107d9d372bb6826bd81d3542a419d6"
    private val uuid = "0b4a4e1c-8b1f-4e4e-9a59-2b7c4b8f1d3e"
    private val user: RemoteUserModel = mockk {
        every { id } returns 1
        every { favoriteMinRating } returns 4
    }
    private val userUseCase: UserUseCase = mockk {
        coEvery { getRemoteUserOrRefresh() } returns Ok(user)
    }
    private val dateDisplayer: DateDisplayer = mockk {
        coEvery { dateString(any()) } returns "May 1"
    }
    private val dateParser: DateParser = mockk {
        every { parseDateOrTimeString(any()) } returns null
    }
    private val remoteMediaUseCase: RemoteMediaUseCase = mockk(relaxed = true)
    private val underTest = MediaUseCase(mockk(), remoteMediaUseCase, userUseCase, dateDisplayer, dateParser)

    @Test
    fun `day collections match local copies by content hash`() = runTest {
        val collections = underTest.toMediaCollection(
            Group("20240501" to listOf(source(uuid, "${md5}1"), source(null, null)))
        )

        val collection = collections.single()
        assertEquals("May 1", collection.displayTitle)
        assertEquals("Berlin", collection.location)
        assertEquals(listOf(MediaItemHashModel(Md5Hash(md5), 1)), collection.mediaItems.map { it.mediaHash })
        assertEquals(uuid, collection.mediaItems.single().id.value)
    }

    @Test
    fun `trashing an item that is both local and remote trashes the remote copy`() {
        val hash = MediaItemHashModel(Md5Hash(md5), 1)
        val local = MediaIdModel.LocalIdModel(1, 0, false, "content://1", "", hash)
        val remote = MediaIdModel.RemoteIdModel(uuid, false, hash)

        underTest.trashMediaItem(MediaIdModel.GroupIdModel(listOf(local, remote), false, hash))
        underTest.restoreMediaItem(MediaIdModel.GroupIdModel(listOf(local, remote), false, hash))
        underTest.trashMediaItem(local)

        verify(exactly = 1) { remoteMediaUseCase.trashMediaItem(uuid) }
        verify(exactly = 1) { remoteMediaUseCase.restoreMediaItem(uuid) }
    }

    @Test
    fun `empty collections are dropped`() = runTest {
        assertTrue(underTest.toMediaCollection(Group("1" to emptyList<MediaCollectionSourceModel>())).isEmpty())
    }

    @Test
    fun `collections need the user`() = runTest {
        coEvery { userUseCase.getRemoteUserOrRefresh() } returns Err(IllegalStateException())

        assertTrue(underTest.toMediaCollection(Group("1" to listOf(source(uuid, "${md5}1")))).isEmpty())
    }

    @Test
    fun `summaries match local copies by content hash`() = runTest {
        val items = with(underTest) { listOf(summary()).mapToMediaItems() }.get()!!

        assertEquals(MediaItemHashModel(Md5Hash(md5), 1), items.single().mediaHash)
        assertEquals(uuid, items.single().id.value)
        assertTrue(items.single().isFavourite)
    }

    private fun source(id: String?, hash: String?) = MediaCollectionSourceModel(
        id = "20240501",
        date = "2024-05-01",
        location = "Berlin",
        mediaItemId = id,
        mediaItemHash = hash,
        dominantColor = null,
        rating = null,
        aspectRatio = null,
        isVideo = false,
        lat = null,
        lon = null,
    )

    private fun summary() = DbRemoteMediaItemSummary(
        id = uuid,
        dominantColor = null,
        url = "${md5}1",
        location = null,
        date = "2024-05-01T10:00:00Z",
        birthTime = null,
        aspectRatio = null,
        type = "image",
        videoLength = null,
        rating = 5,
        gpsLat = null,
        gpsLon = null,
        containerId = "20240501",
    )
}
