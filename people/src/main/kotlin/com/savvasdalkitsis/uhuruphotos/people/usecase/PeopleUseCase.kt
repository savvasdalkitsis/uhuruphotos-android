package com.savvasdalkitsis.uhuruphotos.people.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.people.api.usecase.PeopleUseCase
import com.savvasdalkitsis.uhuruphotos.people.repository.PeopleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) : PeopleUseCase {

    override fun getPeopleByName(): Flow<Result<List<People>, Throwable>> = peopleRepository.getPeopleByName()
        .safelyOnStart {
            refreshPeople()
        }

    override fun getPeopleByPhotoCount(): Flow<Result<List<People>, Throwable>> = peopleRepository.getPeopleByPhotoCount()
        .safelyOnStart {
            refreshPeople()
        }

    override fun getPerson(id: Int): Flow<People> = peopleRepository.getPerson(id)
        .safelyOnStartIgnoring {
            peopleRepository.refreshPerson(id)
        }

    override suspend fun refreshPeople() = peopleRepository.refreshPeople()
}
