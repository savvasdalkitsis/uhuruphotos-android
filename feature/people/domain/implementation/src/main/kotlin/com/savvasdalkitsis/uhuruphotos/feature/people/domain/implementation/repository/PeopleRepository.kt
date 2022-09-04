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
package com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.await
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.model.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.service.PeopleService
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val peopleQueries: PeopleQueries,
    private val peopleService: PeopleService,
) {

    fun observePeopleByName(): Flow<List<People>> = peopleQueries.getPeopleByName()
        .asFlow().mapToList().distinctUntilChanged()

    suspend fun getPeopleByName(): List<People> = peopleQueries.getPeopleByName()
        .await()

    fun observePeopleByPhotoCount(): Flow<List<People>> = peopleQueries.getPeopleByFaceCount()
        .asFlow().mapToList().distinctUntilChanged()

    fun observePerson(id: Int): Flow<People> = peopleQueries.getPerson(id)
        .asFlow().mapToOne().distinctUntilChanged()

    suspend fun refreshPerson(id: Int) = runCatchingWithLog {
        val person = peopleService.getPerson(id)
        async {
            peopleQueries.insertPerson(person.toDbModel())
        }
    }

    suspend fun refreshPeople() = runCatchingWithLog {
        val people = peopleService.getPeople().results
        async {
            peopleQueries.transaction {
                peopleQueries.clearAll()
                for (person in people) {
                    peopleQueries.insertPerson(person.toDbModel())
                }
            }
        }
    }
}
