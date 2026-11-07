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
package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.upload

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class UploadingMediaItemsQueriesTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
        Database.Schema.create(this)
    }
    private val underTest = UploadingMediaItemsQueries(driver)

    @After
    fun tearDown() {
        driver.close()
    }

    private fun queue(id: Long) = underTest.insert(
        UploadingMediaItems(id, "content://media/$id", offset = 0, completed = false, uploadId = "")
    )

    @Test
    fun `queueing an item again keeps its upload progress`() {
        queue(1)
        underTest.updateOffset(offset = 500, id = 1)
        underTest.updateUploadId(uploadId = "upload", id = 1)

        queue(1)

        assertEquals(500L, underTest.getOffset(1).executeAsOne())
        assertEquals("upload", underTest.getUploadId(1).executeAsOne())
    }
}
