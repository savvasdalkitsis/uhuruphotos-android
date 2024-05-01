/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.module.DbModule
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.repository.UserRepository
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.service.UserService
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance

object UserModule {

    val userUseCase: UserUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.usecase.UserUseCase(
            userRepository,
            WelcomeModule.welcomeUseCase,
        )

    private val userRepository: UserRepository
        get() = UserRepository(
            userService,
            DbModule.database.userQueries,
            AuthModule.authenticationUseCase,
        )

    private val userService: UserService by singleInstance {
        AuthModule.ktorfit.create()
    }
}