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
package com.savvasdalkitsis.uhuruphotos.implementation.people.usecase

import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.api.coroutines.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.api.db.people.People
import com.savvasdalkitsis.uhuruphotos.api.people.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.people.repository.PeopleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) : PeopleUseCase {

    override fun observePeopleByName(): Flow<Result<List<People>>> = peopleRepository.observePeopleByName()
        .safelyOnStart {
            refreshPeople()
        }

    override suspend fun getPeopleByName(): List<People> = peopleRepository.getPeopleByName()

    override fun observePeopleByPhotoCount(): Flow<Result<List<People>>> =
        peopleRepository.observePeopleByPhotoCount()
            .safelyOnStart {
                refreshPeople()
            }

    override fun observePerson(id: Int): Flow<People> = peopleRepository.observePerson(id)
        .safelyOnStartIgnoring {
            peopleRepository.refreshPerson(id)
        }

    override suspend fun refreshPeople() = peopleRepository.refreshPeople()
}
