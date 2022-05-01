package com.savvasdalkitsis.uhuruphotos.people.api.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.db.people.People
import kotlinx.coroutines.flow.Flow

interface PeopleUseCase {
    fun getPeopleByName(): Flow<Result<List<People>, Throwable>>
    fun getPeopleByPhotoCount(): Flow<Result<List<People>, Throwable>>
    fun getPerson(id: Int): Flow<People>
    suspend fun refreshPeople()
}