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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.auto.GetPeopleForAutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.people.People

data class Person(
    val name: String,
    val imageUrl: String?,
    val photos: Int,
    val id: Int,
)

suspend fun People.toPerson(urlResolver: suspend (String) -> String?) = Person(
    id = id,
    name = name,
    imageUrl = faceUrl?.let { urlResolver(it) },
    photos = faceCount,
)

suspend fun GetPeopleForAutoAlbum.toPerson(urlResolver: suspend (String) -> String?) = Person(
    id = personId,
    name = name ?: "",
    imageUrl = faceUrl?.let { urlResolver(it) },
    photos = faceCount ?: 0,
)