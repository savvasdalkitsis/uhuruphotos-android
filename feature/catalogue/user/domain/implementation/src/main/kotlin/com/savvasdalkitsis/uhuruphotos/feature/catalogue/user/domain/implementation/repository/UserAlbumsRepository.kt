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
package com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.service.UserAlbumsService
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.domain.implementation.service.model.toUserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbums
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.album.user.UserAlbumsQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitList
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.asFlowList
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserAlbumsRepository @Inject constructor(
    private val userAlbumsQueries: UserAlbumsQueries,
    private val userAlbumsService: UserAlbumsService,
) {

    fun observeUserAlbums(): Flow<List<UserAlbums>> =
        userAlbumsQueries.getUserAlbums().asFlowList()

    suspend fun getUserAlbums(): List<UserAlbums> =
        userAlbumsQueries.getUserAlbums().awaitList()

    suspend fun refreshUserAlbums(): SimpleResult = runCatchingWithLog {
        val albums = userAlbumsService.getUserAlbums()
        userAlbumsQueries.transaction {
            userAlbumsQueries.clearAll()
            for (album in albums.results) {
                userAlbumsQueries.insert(album.toUserAlbums())
            }
        }
    }.simple()
}