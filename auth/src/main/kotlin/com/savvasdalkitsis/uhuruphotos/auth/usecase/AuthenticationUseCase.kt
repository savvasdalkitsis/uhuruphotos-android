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
package com.savvasdalkitsis.uhuruphotos.auth.usecase

import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.*
import com.savvasdalkitsis.uhuruphotos.auth.network.jwt
import com.savvasdalkitsis.uhuruphotos.auth.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationRefreshResponse
import com.savvasdalkitsis.uhuruphotos.db.auth.Token
import com.savvasdalkitsis.uhuruphotos.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.db.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.db.extensions.async
import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.log.log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val authenticationService: AuthenticationService,
) {

    private val mutex = Mutex()

    suspend fun authenticationStatus(): AuthStatus {
        val accessToken = tokenQueries.getAccessToken().awaitSingleOrNull()
        return when {
            accessToken.isNullOrEmpty() || accessToken.jwt.isExpired(0) -> refreshToken()
            else -> Authenticated
        }
    }

    suspend fun login(username: String, password: String) {
        val response = authenticationService.login(AuthenticationCredentials(username, password))
        async {
            tokenQueries.saveToken(
                Token(
                    token = response.refresh,
                    type = TokenType.REFRESH,
                )
            )
        }
        refreshAccessToken(response.refresh)
    }

    suspend fun refreshToken(): AuthStatus = mutex.withLock {
        val refreshToken = tokenQueries.getRefreshToken().awaitSingleOrNull()
        return when {
            refreshToken.isNullOrEmpty() || refreshToken.jwt.isExpired(0) -> Unauthenticated
            else -> try {
                refreshAccessToken(refreshToken)
                Authenticated
            } catch (e: IOException) {
                log(e)
                Offline
            } catch (e: Exception) {
                log(e)
                Unauthenticated
            }
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String): AuthenticationRefreshResponse {
        val response = authenticationService.refreshToken(AuthenticationObtainResponse(refreshToken))
        async {
            tokenQueries.saveToken(
                Token(
                    token = response.access,
                    type = TokenType.ACCESS
                )
            )
        }
        return response
    }
}