package com.savvasdalkitsis.uhuruphotos.people.view.state

import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase

data class Person(
    val name: String,
    val imageUrl: String?,
    val photos: Int,
    val id: Int,
)

context(PhotosUseCase)
suspend fun People.toPerson() = Person(
    id = id,
    name = name,
    imageUrl = faceUrl.toAbsoluteUrl(),
    photos = faceCount,
)