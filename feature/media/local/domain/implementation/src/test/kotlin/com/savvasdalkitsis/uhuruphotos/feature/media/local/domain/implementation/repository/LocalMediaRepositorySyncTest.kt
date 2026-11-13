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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import android.content.ContentResolver
import android.graphics.Color
import android.net.Uri
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.TransactionWithoutReturn
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetailsQueries
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.LocalMediaService
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.usecase.ExifUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.joda.time.format.DateTimeFormat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream

class LocalMediaRepositorySyncTest {

    private val vanished = photo(1)
    private val present = photo(2)
    private val database: Database = mockk {
        every { transaction(any(), any()) } answers {
            secondArg<TransactionWithoutReturn.() -> Unit>().invoke(mockk(relaxed = true))
        }
    }
    private val int = object : ColumnAdapter<Int, Long> {
        override fun decode(databaseValue: Long) = databaseValue.toInt()
        override fun encode(value: Int) = value.toLong()
    }
    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply { Database.Schema.create(this) }
    private val queries = LocalMediaItemDetailsQueries(driver, LocalMediaItemDetails.Adapter(int, int, int, int, int))
    private val service: LocalMediaService = mockk {
        coEvery { getPhotosForBucket(7) } returns listOf(vanished, present)
        coEvery { getVideosForBucket(7) } returns emptyList()
    }
    private val folders: LocalMediaFolderRepository = mockk {
        coEvery { getDefaultLocalFolderId() } returns 7
        every { shouldScanOtherFolders() } returns false
    }
    private val contentResolver: ContentResolver = mockk {
        every { openInputStream(vanished.contentUri) } returns null
        every { openInputStream(present.contentUri) } answers { ByteArrayInputStream(byteArrayOf(1, 2, 3)) }
    }
    private val exifUseCase: ExifUseCase = mockk {
        every { extractFrom(any<java.io.InputStream>()) } returns mockk {
            every { dateTime } returns null
            every { latLon } returns null
        }
    }
    private val thumbnails: LocalMediaThumbnails = mockk(relaxed = true) {
        every { bitmap(any()) } returns null
    }
    private val underTest = LocalMediaRepository(
        database, queries, DownloadingMediaItemsQueries(driver), service, folders, contentResolver, exifUseCase,
        DateTimeFormat.forPattern("yyyy-MM-dd"), thumbnails, mockk(),
    )

    private fun photo(id: Long) = Photo(id, "$id.jpg", 0, 7, "Camera", 1, 1, 1, mockk<Uri>(), "", null)

    @After
    fun tearDown() {
        unmockkStatic(Color::class)
    }

    @Test
    fun `keeps syncing the rest of the folder when an item vanished before it was read`() = runTest {
        mockkStatic(Color::class)
        every { Color.rgb(any<Int>(), any<Int>(), any<Int>()) } returns 0

        underTest.refresh()

        assertEquals(listOf(present.id), queries.getExistingIds().executeAsList())
    }
}
