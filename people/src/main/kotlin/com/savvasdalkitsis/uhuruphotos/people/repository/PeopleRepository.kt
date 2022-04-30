package com.savvasdalkitsis.uhuruphotos.people.repository

import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.db.people.PeopleQueries
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.people.service.PeopleService
import com.savvasdalkitsis.uhuruphotos.people.service.model.toPerson
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import java.io.IOException
import javax.inject.Inject

class PeopleRepository @Inject constructor(
    private val peopleQueries: PeopleQueries,
    private val peopleService: PeopleService,
){

    fun getPeopleByName(): Flow<List<People>> = peopleQueries.getPeopleByName()
        .asFlow().mapToList().distinctUntilChanged()

    fun getPeopleByPhotoCount(): Flow<List<People>> = peopleQueries.getPeopleByFaceCount()
        .asFlow().mapToList().distinctUntilChanged()

    fun getPerson(id: Int): Flow<People> = peopleQueries.getPerson(id)
        .asFlow().mapToOne().distinctUntilChanged()

    suspend fun refreshPerson(id: Int) {
        val person = peopleService.getPerson(id)
        crud {
            peopleQueries.insertPerson(person.toPerson())
        }
    }

    suspend fun refreshPeople() {
        try {
            val people = peopleService.getPeople().results
            crud {
                peopleQueries.transaction {
                    peopleQueries.deleteAll()
                    for (person in people) {
                        peopleQueries.insertPerson(person.toPerson())
                    }
                }
            }
        } catch (e: IOException) {
            log(e)
        }
    }
}
