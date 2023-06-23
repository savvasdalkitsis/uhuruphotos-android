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

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.service.UserService
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.andThenTry
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userQueries: UserQueries,
    private val authenticationUseCase: AuthenticationUseCase,
) {

    fun observeUser(): Flow<User> = userQueries.getUser().asFlow().mapToOneNotNull().distinctUntilChanged()

    suspend fun getUser(): User? = userQueries.getUser().awaitSingleOrNull()

    suspend fun refreshUser(): Result<User, Throwable> = authenticationUseCase.getUserIdFromToken()
        .andThenTry { id ->
            userService.getUser(id)
        }.map { userResult ->
            async { userQueries.addUser(userResult.toUser()) }
            userResult.toUser()
        }
}