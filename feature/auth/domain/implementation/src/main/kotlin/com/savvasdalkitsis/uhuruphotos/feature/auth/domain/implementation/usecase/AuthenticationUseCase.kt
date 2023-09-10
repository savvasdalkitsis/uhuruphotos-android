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

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Offline
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.ServerDown
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken.Expired
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken.NotFound
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.ServerToken.Valid
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import se.ansman.dagger.auto.AutoBind
import java.io.IOException
import javax.inject.Inject

private const val EXPIRED = "EXPIRED"

@AutoBind
class AuthenticationUseCase @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val authenticationService: AuthenticationService,
    private val preferences: Preferences,
    private val jwtUseCase: JwtUseCase,
) : AuthenticationUseCase {

    private val mutex = Mutex()
    private val userKey = "userIdFromToken"

    override suspend fun authenticationStatus(): AuthStatus {
        val accessToken = getAccessToken()
        return when {
            accessToken.isNullOrEmpty() || accessToken.isExpired -> refreshToken()
            else -> {
                preferences.set(userKey, accessToken.userId)
                Authenticated
            }
        }
    }

    override fun observeRefreshToken(): Flow<ServerToken> =
        tokenQueries.getRefreshToken().asFlow().mapToOneOrNull(Dispatchers.IO).map {
            when {
                it.isNullOrEmpty() -> NotFound
                it.isExpired -> Expired
                else -> Valid(it)
            }
        }

    private suspend fun getAccessToken(): String? = tokenQueries.getAccessToken().awaitSingleOrNull()

    override suspend fun refreshToken(): AuthStatus = mutex.withLock {
        val refreshToken = tokenQueries.getRefreshToken().awaitSingleOrNull()
        return when {
            refreshToken.isNullOrEmpty() || refreshToken.isExpired -> Unauthenticated()
            else -> refreshAccessToken(refreshToken)
        }.also {
            if (it is Unauthenticated) {
                clearTokensForAuthenticationLoss()
            }
        }
    }

    private fun clearTokensForAuthenticationLoss() {
        tokenQueries.delete(TokenType.ACCESS)
        tokenQueries.saveToken(Token(
            token = EXPIRED,
            type = TokenType.REFRESH,
        ))
    }

    override suspend fun refreshAccessToken(refreshToken: String): AuthStatus = try {
        val response = authenticationService.refreshToken(AuthenticationObtainResponse(refreshToken))
        val refreshResponse = response.body()
        when {
            refreshResponse != null -> {
                async {
                    val token = refreshResponse.access
                    preferences.set(userKey, token.userId)
                    tokenQueries.saveToken(
                        Token(
                            token = token,
                            type = TokenType.ACCESS
                        )
                    )
                }
                Authenticated
            }
            response.code() >= 500 || response.code() == 404 -> ServerDown
            else -> Unauthenticated(response.code())
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

    private fun idFromPref() = preferences.get<String?>(userKey, null)

    private val String.userId: String get() = with(jwtUseCase) {
        return this@userId["user_id"]
    }

    private val String.isExpired: Boolean get() = this == EXPIRED || with(jwtUseCase) {
        expired()
    }
}