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
package com.savvasdalkitsis.uhuruphotos.user.repository

import com.savvasdalkitsis.uhuruphotos.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.db.user.User
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.user.service.UserService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userQueries: UserQueries,
) {

    fun observeUser(): Flow<User> = userQueries.getUser().asFlow().mapToOneNotNull()

    suspend fun getUser(): User? = userQueries.getUser().awaitSingleOrNull()

    suspend fun refreshUser(): User? = try {
        val userResults = userService.getUser()
        for (userResult in userResults.results) {
            async { userQueries.addUser(userResult.toUser()) }
        }
        userResults.results.last().toUser()
    } catch (e: IOException) {
        log(e)
        null
    }
}