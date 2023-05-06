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

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.network.jwt
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject

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

    override suspend fun refreshToken(): AuthStatus = mutex.withLock {
        val refreshToken = tokenQueries.getRefreshToken().awaitSingleOrNull()
        return when {
            refreshToken.isNullOrEmpty() || refreshToken.jwt.isExpired(0) -> Unauthenticated
            else -> refreshAccessToken(refreshToken)
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): AuthStatus = try {
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