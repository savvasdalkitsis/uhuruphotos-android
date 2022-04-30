package com.savvasdalkitsis.uhuruphotos.people.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStart
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.people.repository.PeopleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeopleUseCase @Inject constructor(
    private val peopleRepository: PeopleRepository,
) {

    fun getPeopleByName(): Flow<Result<List<People>, Throwable>> = peopleRepository.getPeopleByName()
        .safelyOnStart {
            refreshPeople()
        }

    fun getPeopleByPhotoCount(): Flow<Result<List<People>, Throwable>> = peopleRepository.getPeopleByPhotoCount()
        .safelyOnStart {
            refreshPeople()
        }

    fun getPerson(id: Int): Flow<People> = peopleRepository.getPerson(id)
        .safelyOnStartIgnoring {
            peopleRepository.refreshPerson(id)
        }

    suspend fun refreshPeople() = peopleRepository.refreshPeople()
}
