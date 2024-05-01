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

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.RemoteUserModel
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.User
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.User.LocalUser
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.User.RemoteUser
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.model.UserNotRemoteException
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.repository.UserRepository
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.flow
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.get
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onIO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class UserUseCase(
    private val userRepository: UserRepository,
    private val welcomeUseCase: WelcomeUseCase,
) : UserUseCase {

    override fun observeUser(): Flow<User> =
        welcomeUseCase.flow(
            withoutRemoteAccess = flowOf(LocalUser),
            withRemoteAccess = userRepository.observeUser()
                .onEach { user ->
                    if (user == null) {
                        onIO { userRepository.refreshUser() }
                    }
                }
                .filterNotNull()
                .map { RemoteUser(it) }
        )

    override suspend fun getRemoteUserOrRefresh(): Result<RemoteUserModel, Throwable> =
        welcomeUseCase.get(
            withoutRemoteAccess = { Err(UserNotRemoteException()) },
            withRemoteAccess = {
                userRepository.getUser()?.let { Ok(it) } ?: userRepository.refreshUser()
            }
        )

    override suspend fun refreshRemoteUser() {
        if (welcomeUseCase.getWelcomeStatus().hasRemoteAccess) {
            userRepository.refreshUser()
        }
    }
}