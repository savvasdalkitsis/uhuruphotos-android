package com.savvasdalkitsis.uhuruphotos.people.service

import com.savvasdalkitsis.uhuruphotos.people.service.model.PeopleResult
import com.savvasdalkitsis.uhuruphotos.people.service.model.PersonResult
import retrofit2.http.GET
import retrofit2.http.Path

interface PeopleService {

    @GET("/api/persons/")
    suspend fun getPeople(): PeopleResult

    @GET("/api/persons/{id}/")
    suspend fun getPerson(@Path("id") id: Int): PersonResult

}
