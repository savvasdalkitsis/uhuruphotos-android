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

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.service.http.response.toDbModel
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.service.http.PeopleService
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.async
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val peopleQueries: PeopleQueries,
    private val peopleService: PeopleService,
) {

    fun observePeopleByName(): Flow<List<People>> = peopleQueries.getPeopleByName()
        .asFlow().mapToList(Dispatchers.IO).distinctUntilChanged()

    suspend fun getPeopleByName(): List<People> = peopleQueries.getPeopleByName()
        .awaitList()

    fun observePeopleByPhotoCount(): Flow<List<People>> = peopleQueries.getPeopleByFaceCount()
        .asFlow().mapToList(Dispatchers.IO).distinctUntilChanged()

    fun observePerson(id: Int): Flow<People> = peopleQueries.getPerson(id)
        .asFlow().mapToOne(Dispatchers.IO).distinctUntilChanged()

    suspend fun refreshPerson(id: Int) = runCatchingWithLog {
        peopleService.getPerson(id).toDbModel()?.let {
            peopleQueries.insertPerson(it)
        }
    }.simple()

    suspend fun refreshPeople() = runCatchingWithLog {
        val people = peopleService.getPeople().results
        async {
            peopleQueries.transaction {
                peopleQueries.clearAll()
                people.mapNotNull { it.toDbModel() }.forEach {
                    peopleQueries.insertPerson(it)
                }
            }
        }
    }.simple()
}
