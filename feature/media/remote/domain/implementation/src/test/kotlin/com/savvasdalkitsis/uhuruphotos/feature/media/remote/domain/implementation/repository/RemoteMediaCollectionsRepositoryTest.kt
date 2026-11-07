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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.repository

import app.cash.sqldelight.Query
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.media.DbRemoteMediaCollections
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaCollectionsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.model.RemoteMediaCollectionsFilterModel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDayCompleteResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaDaySummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.service.http.response.RemoteMediaItemSummaryResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.RemoteMediaCollectionsService
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteDayPhotosResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteDayResponse
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteDaysResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteFeedDayResponseData
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.service.http.response.RemoteFeedResponseData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class RemoteMediaCollectionsRepositoryTest {

    private val service: RemoteMediaCollectionsService = mockk()
    private val queries: RemoteMediaCollectionsQueries = mockk()
    private val underTest = RemoteMediaCollectionsRepository(service, queries)
    private val timeline = RemoteMediaCollectionsFilterModel.Timeline
    private val timelineQuery = mapOf("archived" to "false")

    @Test
    fun `lists days from the days api`() = runTest {
        coEvery { service.getDays(timelineQuery) } returns days(day("20240501", 3), day("1", 1))

        val collections = underTest.getCollections(timeline)

        assertEquals(listOf("20240501", "1"), collections.map { it.id })
        assertTrue(collections.all { it.incomplete })
    }

    @Test
    fun `falls back to the date albums of servers without the days api`() = runTest {
        coEvery { service.getDays(any()) } throws notFound()
        coEvery { service.getLegacyDays(timelineQuery) } returns RemoteFeedResponseData(listOf(legacyDay("7")))
        coEvery { service.getLegacyDay("7", 1, timelineQuery) } returns legacyPage("7", 1, summary("a"))

        assertEquals(listOf("7"), underTest.getCollections(timeline).map { it.id })
        assertEquals(listOf("a"), underTest.getCollection("7", timeline).items.map { it.id })
        coVerify(exactly = 0) { service.getDayPhotos(any(), any(), any(), any()) }
    }

    @Test
    fun `goes back to the days api once the server has it`() = runTest {
        coEvery { service.getDays(any()) } throws notFound() andThen days(day("20240501", 1))
        coEvery { service.getLegacyDays(any()) } returns
            RemoteFeedResponseData(listOf(legacyDay("7"))) andThenThrows notFound()

        underTest.getCollections(timeline)

        assertEquals(listOf("20240501"), underTest.getCollections(timeline).map { it.id })
    }

    @Test
    fun `other errors are not treated as an old server`() = runTest {
        coEvery { service.getDays(any()) } throws httpError(500)

        val result = runCatching { underTest.getCollections(timeline) }

        assertTrue(result.exceptionOrNull() is HttpException)
        coVerify(exactly = 0) { service.getLegacyDays(any()) }
    }

    @Test
    fun `fetches every page of a day by cursor`() = runTest {
        coEvery { service.getDayPhotos("20240501", null, 500, timelineQuery) } returns
            RemoteDayPhotosResponseData(listOf(summary("a"), summary("b")), next = "b")
        coEvery { service.getDayPhotos("20240501", "b", 500, timelineQuery) } returns
            RemoteDayPhotosResponseData(listOf(summary("c")), next = null)

        val day = underTest.getCollection("20240501", timeline)

        assertEquals(listOf("a", "b", "c"), day.items.map { it.id })
        assertEquals(3, day.numberOfItems)
    }

    @Test
    fun `a day missing on both apis is empty`() = runTest {
        coEvery { service.getDayPhotos(any(), any(), any(), any()) } throws notFound()
        coEvery { service.getLegacyDay(any(), any(), any()) } throws notFound()

        assertEquals(emptyList<RemoteMediaItemSummaryResponse>(), underTest.getCollection("1", timeline).items)
    }

    @Test
    fun `a day of an old server switches to the legacy api`() = runTest {
        coEvery { service.getDayPhotos(any(), any(), any(), any()) } throws notFound()
        coEvery { service.getLegacyDay("7", 1, timelineQuery) } returns legacyPage("7", 1, summary("a"))

        assertEquals(listOf("a"), underTest.getCollection("7", timeline).items.map { it.id })
        assertEquals(listOf("a"), underTest.getCollection("7", timeline).items.map { it.id })
        coVerify(exactly = 1) { service.getDayPhotos(any(), any(), any(), any()) }
    }

    @Test
    fun `a legacy day stops paging at an empty page`() = runTest {
        coEvery { service.getDays(any()) } throws notFound()
        coEvery { service.getLegacyDays(any()) } returns RemoteFeedResponseData(emptyList())
        coEvery { service.getLegacyDay("7", 1, any()) } returns legacyPage("7", 3, summary("a"))
        coEvery { service.getLegacyDay("7", 2, any()) } returns legacyPage("7", 3, summary("b"))
        coEvery { service.getLegacyDay("7", 3, any()) } returns legacyPage("7", 3)
        underTest.getCollections(timeline)

        assertEquals(listOf("a", "b"), underTest.getCollection("7", timeline).items.map { it.id })
    }

    @Test
    fun `a missing legacy day is empty`() = runTest {
        coEvery { service.getDays(any()) } throws notFound()
        coEvery { service.getLegacyDays(any()) } returns RemoteFeedResponseData(emptyList())
        coEvery { service.getLegacyDay(any(), any(), any()) } throws notFound()
        underTest.getCollections(timeline)

        assertEquals(0, underTest.getCollection("7", timeline).items.size)
    }

    @Test
    fun `an incremental refresh only lists days whose count changed`() = runTest {
        coEvery { service.getDays(timelineQuery) } returns
            days(day("20240501", 3), day("20240502", 2), day("20240503", 1))
        every { queries.get("20240501") } returns query(collection("20240501", 3))
        every { queries.get("20240502") } returns query(collection("20240502", 5))
        every { queries.get("20240503") } returns query(null)

        val changed = underTest.getCollections(timeline.copy(modifiedSince = "2026-01-01T00:00:00Z"))

        assertEquals(listOf("20240502", "20240503"), changed.map { it.id })
    }

    @Test
    fun `legacy incremental refresh sends last_modified`() = runTest {
        val since = "2026-01-01T00:00:00Z"
        coEvery { service.getDays(any()) } throws notFound()
        coEvery { service.getLegacyDays(timelineQuery + ("last_modified" to since)) } returns
            RemoteFeedResponseData(listOf(legacyDay("7")))

        assertEquals(listOf("7"), underTest.getCollections(timeline.copy(modifiedSince = since)).map { it.id })
    }

    private fun days(vararg days: RemoteDayResponse) = RemoteDaysResponseData(days.toList())

    private fun day(id: String, count: Int) = RemoteDayResponse(id, "2024-05-01", "", count)

    private fun legacyDay(id: String) = RemoteMediaDaySummaryResponse(id = id, date = null, numberOfItems = 1)

    private fun legacyPage(id: String, total: Int, vararg items: RemoteMediaItemSummaryResponse) =
        RemoteFeedDayResponseData(
            RemoteMediaDayCompleteResponse(id = id, date = null, numberOfItems = total, items = items.toList())
        )

    private fun summary(id: String) = RemoteMediaItemSummaryResponse(id = id, date = null)

    private fun collection(id: String, count: Int) = DbRemoteMediaCollections(id, null, null, null, true, count)

    private fun query(result: DbRemoteMediaCollections?): Query<DbRemoteMediaCollections> = mockk {
        every { executeAsOneOrNull() } returns result
    }

    private fun notFound() = httpError(404)

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))
}
