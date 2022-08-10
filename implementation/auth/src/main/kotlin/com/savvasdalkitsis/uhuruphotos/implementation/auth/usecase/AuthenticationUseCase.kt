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
package com.savvasdalkitsis.uhuruphotos.implementation.auth.usecase

import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.api.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.api.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.api.db.auth.Token
import com.savvasdalkitsis.uhuruphotos.api.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.api.db.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.api.log.log
import com.savvasdalkitsis.uhuruphotos.implementation.auth.network.jwt
import com.savvasdalkitsis.uhuruphotos.implementation.auth.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.implementation.auth.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.implementation.auth.service.model.AuthenticationObtainResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val authenticationService: AuthenticationService,
) : AuthenticationUseCase {

    private val mutex = Mutex()

    override suspend fun authenticationStatus(): AuthStatus {
        val accessToken = tokenQueries.getAccessToken().awaitSingleOrNull()
        return when {
            accessToken.isNullOrEmpty() || accessToken.jwt.isExpired(0) -> refreshToken()
            else -> Authenticated
        }
    }

    override suspend fun login(username: String, password: String): AuthStatus {
        val response = authenticationService.login(AuthenticationCredentials(username, password))
        async {
            tokenQueries.saveToken(
                Token(
                    token = response.refresh,
                    type = TokenType.REFRESH,
                )
            )
        }
        return refreshAccessToken(response.refresh)
    }

    override suspend fun refreshToken(): AuthStatus = mutex.withLock {
        val refreshToken = tokenQueries.getRefreshToken().awaitSingleOrNull()
        return when {
            refreshToken.isNullOrEmpty() || refreshToken.jwt.isExpired(0) -> Unauthenticated
            else -> refreshAccessToken(refreshToken)
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String): AuthStatus = try {
        val response = authenticationService.refreshToken(AuthenticationObtainResponse(refreshToken))
        val refreshResponse = response.body()
        when {
            refreshResponse != null -> {
                async {
                    tokenQueries.saveToken(
                        Token(
                            token = refreshResponse.access,
                            type = TokenType.ACCESS
                        )
                    )
                }
                Authenticated
            }
            response.code() >= 500 || response.code() == 404 -> ServerDown
            else -> Unauthenticated
        }
    } catch (e: IOException) {
        log(e)
        Offline
    } catch (e: Exception) {
        log(e)
        Unauthenticated
    }
}