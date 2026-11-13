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
package com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.repository

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.PeopleQueries
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PeopleRepositoryTest {

    private val int = object : ColumnAdapter<Int, Long> {
        override fun decode(databaseValue: Long) = databaseValue.toInt()
        override fun encode(value: Int) = value.toLong()
    }
    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply { Database.Schema.create(this) }
    private val peopleQueries = PeopleQueries(driver, People.Adapter(int, int))
    private val underTest = PeopleRepository(peopleQueries, mockk())

    private fun person(id: Int, name: String) = People(id, name, 1, null, null)

    @Test
    fun `waits for a person that is not stored`() = runTest {
        val person = withContext(Dispatchers.Default) {
            withTimeoutOrNull(1000) { underTest.observePerson(7).first() }
        }

        assertNull(person)
    }

    @Test
    fun `keeps observing a person that gets pruned and comes back`() = runTest {
        peopleQueries.insertPerson(person(7, "Ada"))

        underTest.observePerson(7).test {
            assertEquals("Ada", awaitItem().name)
            peopleQueries.clearAll()
            Thread.sleep(500)
            peopleQueries.insertPerson(person(7, "Ada L."))

            assertEquals("Ada L.", awaitItem().name)
        }
    }
}
