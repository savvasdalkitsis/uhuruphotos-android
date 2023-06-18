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

import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationLoginUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.Credentials
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import dagger.hilt.android.scopes.ActivityRetainedScoped
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class AuthenticationLoginUseCase @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val authenticationUseCase: AuthenticationUseCase,
    private val authenticationService: AuthenticationService,
    private val credentialManager: CredentialManager,
    private val currentActivityHolder: CurrentActivityHolder,
) : AuthenticationLoginUseCase {

    private val activity get() = currentActivityHolder.currentActivity

    override suspend fun login(credentials: Credentials): Result<AuthStatus, Throwable> =
        runCatchingWithLog {
            val response = authenticationService.login(AuthenticationCredentials(
                credentials.username,
                credentials.password,
            ))
            async {
                tokenQueries.saveToken(
                    Token(
                        token = response.refresh,
                        type = TokenType.REFRESH,
                    )
                )
            }
            val authStatus = authenticationUseCase.refreshAccessToken(response.refresh)
            if (authStatus is AuthStatus.Authenticated) {
                saveCredentials(credentials)
            }
            return Ok(authStatus)
        }

    override suspend fun loadSavedCredentials(): Credentials? = activity?.let { activity ->
        try {
            val result = credentialManager.getCredential(
                GetCredentialRequest(listOf(GetPasswordOption())),
                activity,
            )
            when (val credentials = result.credential) {
                is PasswordCredential -> Credentials(credentials.id, credentials.password)
                else -> null
            }
        } catch (e: Exception) {
            log(e)
            null
        }
    }

    private suspend fun saveCredentials(credentials: Credentials) {
        activity?.let { activity ->
            try {
                credentialManager.createCredential(
                    CreatePasswordRequest(credentials.username, credentials.password), activity
                )
            } catch (e: Exception) {
                log(e)
            }
        }
    }
}