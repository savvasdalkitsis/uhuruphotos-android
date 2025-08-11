package com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState

fun People.toPerson(urlResolver: (String) -> String?) = PersonState(
    id = id,
    name = name,
    imageUrl = faceUrl?.let { urlResolver(it) },
    photos = faceCount,
)

suspend fun GetPeopleForAutoAlbum.toPerson(urlResolver: suspend (String) -> String?) = PersonState(
    id = personId,
    name = name ?: "",
    imageUrl = faceUrl?.let { urlResolver(it) },
    photos = faceCount ?: 0,
)