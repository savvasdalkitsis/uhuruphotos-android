/*
Copyright 2026 Savvas Dalkitsis

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

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.AutoAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumPhotosQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaItemSummaryQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.remote.RemoteMediaTrashQueries
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteMediaRepositoryTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply { Database.Schema.create(this) }
    private val autoAlbumPhotos = AutoAlbumPhotosQueries(driver)
    private val userAlbumPhotos = UserAlbumPhotosQueries(driver)
    private val underTest = RemoteMediaRepository(
        remoteMediaCollectionsQueries = mockk(),
        remoteMediaItemDetailsQueries = RemoteMediaItemDetailsQueries(driver, mockk()),
        remoteMediaItemSummaryQueries = RemoteMediaItemSummaryQueries(driver, mockk()),
        remoteMediaService = mockk(),
        remoteMediaTrashQueries = RemoteMediaTrashQueries(driver, mockk()),
        lightboxDetailsQueries = mockk(),
        autoAlbumPhotosQueries = autoAlbumPhotos,
        userAlbumPhotosQueries = userAlbumPhotos,
    )

    private fun hashesIn(table: String): List<String> = driver.executeQuery(
        identifier = null,
        sql = "SELECT imageHash FROM $table ORDER BY imageHash",
        mapper = { cursor ->
            QueryResult.Value(buildList { while (cursor.next().value) add(cursor.getString(0)!!) })
        },
        parameters = 0,
    ).value

    @Test
    fun `removes a deleted item from the event albums it was in`() {
        autoAlbumPhotos.insert("deleted", "1")
        autoAlbumPhotos.insert("kept", "1")

        underTest.deleteMediaItem("deleted")

        assertEquals(listOf("kept"), hashesIn("autoAlbumPhotos"))
    }

    @Test
    fun `removes a deleted item from the user albums it was in`() {
        userAlbumPhotos.insert("deleted", "1")
        userAlbumPhotos.insert("kept", "2")

        underTest.deleteMediaItem("deleted")

        assertEquals(listOf("kept"), hashesIn("userAlbumPhotos"))
    }
}
