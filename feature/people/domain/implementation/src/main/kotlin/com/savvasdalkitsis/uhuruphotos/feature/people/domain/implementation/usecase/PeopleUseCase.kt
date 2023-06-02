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
package com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.domain.implementation.repository.PeopleRepository
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.onStartWithResult
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import kotlinx.coroutines.flow.Flow
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class PeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) : PeopleUseCase {

    override fun observePeopleByName(): Flow<List<People>> = peopleRepository.observePeopleByName()

    override suspend fun getPeopleByName(): List<People> = peopleRepository.getPeopleByName()

    override fun observePeopleByPhotoCount(): Flow<Result<List<People>>> =
        peopleRepository.observePeopleByPhotoCount()
            .onStartWithResult {
                refreshPeople()
            }

    override fun observePerson(id: Int): Flow<People> = peopleRepository.observePerson(id)
        .safelyOnStartIgnoring {
            peopleRepository.refreshPerson(id)
        }

    override suspend fun refreshPeople() = peopleRepository.refreshPeople()
}
