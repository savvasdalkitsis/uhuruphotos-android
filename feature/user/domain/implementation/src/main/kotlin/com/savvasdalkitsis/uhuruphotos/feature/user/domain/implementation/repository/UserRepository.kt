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
package com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.api.db.user.User
import com.savvasdalkitsis.uhuruphotos.api.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.service.UserService
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userQueries: UserQueries,
) {

    fun observeUser(): Flow<User> = userQueries.getUser().asFlow().mapToOneNotNull()

    suspend fun getUser(): User? = userQueries.getUser().awaitSingleOrNull()

    suspend fun refreshUser(): Result<User> = runCatchingWithLog {
        val userResults = userService.getUser()
        for (userResult in userResults.results) {
            async { userQueries.addUser(userResult.toUser()) }
        }
        userResults.results.last().toUser()
    }
}