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
package com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.usecase

import android.app.DownloadManager
import android.database.Cursor
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.download.DownloadingMediaItemsQueries
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.DownloadId
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.DownloadingRepository
import com.savvasdalktsis.uhuruphotos.feature.download.domain.implementation.repository.MediaId
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DownloadUseCaseTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply { Database.Schema.create(this) }
    private val repository = DownloadingRepository(DownloadingMediaItemsQueries(driver))
    private val downloadManager = mockk<DownloadManager>()
    private val underTest = DownloadUseCase(downloadManager, repository, mockk(), mockk(), mockk(), mockk())

    @Test
    fun `keeps downloads when the download manager cannot be queried`() = runBlocking {
        repository.setDownloading(MediaId("photo"), DownloadId(5))
        every { downloadManager.query(any()) } returns null

        underTest.clearFailuresAndStale()

        assertEquals(setOf("photo"), underTest.getDownloading())
    }

    @Test
    fun `clears downloads the download manager no longer runs`() = runBlocking {
        repository.setDownloading(MediaId("running"), DownloadId(5))
        repository.setDownloading(MediaId("failed"), DownloadId(6))
        every { downloadManager.query(any()) } returns cursorOf(5)

        underTest.clearFailuresAndStale()

        assertEquals(setOf("running"), underTest.getDownloading())
    }

    private fun cursorOf(vararg ids: Long): Cursor {
        val rows = ids.iterator()
        var current = 0L
        return mockk(relaxed = true) {
            every { moveToNext() } answers { rows.hasNext().also { if (it) current = rows.next() } }
            every { getColumnIndex(DownloadManager.COLUMN_ID) } returns 0
            every { getLong(0) } answers { current }
        }
    }
}
