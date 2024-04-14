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

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOr
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken.Expired
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken.NotFound
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken.Valid
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationLoginUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.EXPIRED
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationRefreshRequest
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import se.ansman.dagger.auto.AutoBind
import java.io.IOException
import javax.inject.Inject

@AutoBind
class AuthenticationUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val jwtUseCase: JwtUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val authenticationLoginUseCase: AuthenticationLoginUseCase,
) : AuthenticationUseCase {

    private val mutex = Mutex()

    override suspend fun authenticationStatus(): AuthStatus {
        val accessToken = authenticationRepository.getAccessToken()
        return when {
            accessToken.isNullOrEmpty() || accessToken.isExpired -> refreshToken()
            else -> {
                authenticationRepository.setUserIdFromToken(accessToken)
                Authenticated
            }
        }
    }

    override fun observeRefreshToken(): Flow<ServerToken> =
        authenticationRepository.observeRefreshToken().map {
            when {
                it.isNullOrEmpty() -> NotFound
                it.isExpired -> Expired
                else -> Valid(it)
            }
        }

    override suspend fun refreshToken(): AuthStatus = mutex.withLock {
        val refreshToken = authenticationRepository.getRefreshToken()
        val refreshStatus = when {
            refreshToken.isNullOrEmpty() || refreshToken.isExpired -> Unauthenticated()
            else -> refreshAccessToken(refreshToken)
        }
        return when (refreshStatus) {
            is Unauthenticated -> attemptAutoLogin().also { newAttempt ->
                if (newAttempt is Unauthenticated) {
                    authenticationRepository.clearTokensForAuthenticationLoss()
                }
            }
            else -> refreshStatus
        }
    }

    private suspend fun attemptAutoLogin(): AuthStatus = with(authenticationLoginUseCase) {
        val credentials = loadSavedCredentials()
        when {
            credentials == null || credentials.isEmpty -> Unauthenticated()
            else -> login(credentials, rememberCredentials = true).getOr(Unauthenticated())
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): AuthStatus = try {
        val response = authenticationService.refreshToken(AuthenticationRefreshRequest(refreshToken))
        val refreshResponse = response.body()
        when {
            refreshResponse != null -> {
                async {
                    authenticationRepository.saveAccessToken(refreshResponse.access)
                }
                Authenticated
            }
            response.code >= 500 || response.code == 404 -> ServerDown
            else -> Unauthenticated(response.code)
        }
    } catch (e: IOException) {
        log(e)
        Offline
    } catch (e: Exception) {
        log(e)
        Unauthenticated()
    }

    override suspend fun getUserIdFromToken(): Result<String, Throwable> =
        when (val id = idFromPref()) {
            null -> {
                refreshToken()
                idFromPref()?.let { Ok(it) }
                    ?: Err(IllegalStateException("Could not refresh user id from token"))
            }
            else -> Ok(id)
        }

    private fun idFromPref() = authenticationRepository.getUserId()

    private val String.isExpired: Boolean get() = this == EXPIRED || with(jwtUseCase) {
        expired()
    }
}