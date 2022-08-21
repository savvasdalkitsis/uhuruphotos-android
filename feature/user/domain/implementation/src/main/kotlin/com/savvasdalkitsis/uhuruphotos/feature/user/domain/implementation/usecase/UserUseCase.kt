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
package com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.api.db.user.User
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.repository.UserRepository
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) : UserUseCase {

    override fun observeUser(): Flow<User> = userRepository.observeUser()
        .safelyOnStartIgnoring {
            userRepository.refreshUser()
        }

    override suspend fun getUserOrRefresh(): Result<User> =
        userRepository.getUser()?.let { Result.success(it) } ?: userRepository.refreshUser()
}