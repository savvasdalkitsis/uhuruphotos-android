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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationLoginUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.Credentials
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.toAuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.EncryptedPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class AuthenticationLoginUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    @EncryptedPreferences
    private val preferences: Preferences,
    private val authenticationRepository: AuthenticationRepository,
) : AuthenticationLoginUseCase {

    private val keyUserName = "auth:keyUserName"
    private val keyPassword = "auth:keyPassword"

    override suspend fun login(
        credentials: Credentials,
        rememberCredentials: Boolean,
    ): Result<AuthStatus, Throwable> = runCatchingWithLog {
        val response = authenticationService.login(credentials.toAuthenticationCredentials)
        authenticationRepository.saveRefreshToken(response.refresh)
        authenticationRepository.saveAccessToken(response.access)
        if (rememberCredentials) {
            saveCredentials(credentials)
        } else {
            clearCredentials()
        }
        return Ok(Authenticated)
    }

    override suspend fun loadSavedCredentials(): Credentials? =
        keyUserName.pref?.let { username ->
            keyPassword.pref?.let { pass ->
                Credentials(username, pass)
            }
        }

    private fun clearCredentials() {
        preferences.remove(keyUserName)
        preferences.remove(keyPassword)
    }

    private fun saveCredentials(credentials: Credentials) {
        keyUserName.pref = credentials.username
        keyPassword.pref = credentials.password
    }

    private var String.pref
        get() = preferences.getNullableString(this, null)
        set(value) { preferences.set(this, value) }
}