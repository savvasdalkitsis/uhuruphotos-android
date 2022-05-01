package com.savvasdalkitsis.uhuruphotos.people.api.view.state

import com.savvasdalkitsis.uhuruphotos.db.people.People

data class Person(
    val name: String,
    val imageUrl: String?,
    val photos: Int,
    val id: Int,
)

suspend fun People.toPerson(urlResolver: suspend (String?) -> String?) = Person(
    id = id,
    name = name,
    imageUrl = urlResolver(faceUrl),
    photos = faceCount,
)