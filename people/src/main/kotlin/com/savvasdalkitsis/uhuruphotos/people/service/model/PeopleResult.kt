package com.savvasdalkitsis.uhuruphotos.people.service.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PeopleResult(val results: List<PersonResult>)