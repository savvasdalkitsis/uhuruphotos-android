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

import android.util.Base64
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import se.ansman.dagger.auto.AutoBind
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset
import javax.inject.Inject

@AutoBind
class AuthenticationUseCase @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val authenticationService: AuthenticationService,
    private val preferences: Preferences,
) : AuthenticationUseCase {

    private val mutex = Mutex()
    private val userKey = "userIdFromToken"

    override suspend fun authenticationStatus(): AuthStatus {
        val accessToken = getAccessToken()
        return when {
            accessToken.isNullOrEmpty() || accessToken.jwt.isExpired(0) -> refreshToken()
            else -> {
                preferences.set(userKey, accessToken.userId)
                Authenticated
            }
        }
    }

    override fun observeAccessToken(): Flow<String?> =
        tokenQueries.getAccessToken().asFlow().mapToOneOrNull(Dispatchers.IO)

    override fun observeRefreshToken(): Flow<String?> =
        tokenQueries.getRefreshToken().asFlow().mapToOneOrNull(Dispatchers.IO)

    override suspend fun getAccessToken(): String? = tokenQueries.getAccessToken().awaitSingleOrNull()

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
            else -> Unauthenticated
        }
    } catch (e: IOException) {
        log(e)
        Offline
    } catch (e: Exception) {
        log(e)
        Unauthenticated
    }

    override suspend fun getUserIdFromToken(): Result<String, Throwable> =
        when (val id = idFromPref()) {
            null -> {
                refreshToken()
                idFromPref()?.let { Ok(it) }
                    ?: Err(IllegalStateException("Could not refresh user if from token"))
            }
            else -> Ok(id)
        }

    private fun idFromPref() = preferences.get<String?>(userKey, null)

    private val String.userId: String
        get() {
            val parts: Array<String> = splitToken(this)
            val mapType: Type = object : TypeToken<Map<String, String>>() {}.type
            val payload = Gson().fromJson<Map<String, String>>(base64Decode(parts[1]), mapType)
            return payload["user_id"].toString()
        }

    private fun base64Decode(string: String): String {
        val decoded: String = try {
            val bytes =
                Base64.decode(string, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            String(bytes, Charset.defaultCharset())
        } catch (e: java.lang.IllegalArgumentException) {
            throw IllegalArgumentException(
                "Received bytes didn't correspond to a valid Base64 encoded string.",
                e
            )
        }
        return decoded
    }

    private fun splitToken(token: String): Array<String> {
        var parts: Array<String> = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (parts.size == 2 && token.endsWith(".")) {
            parts = arrayOf(parts[0], parts[1], "")
        }
        if (parts.size != 3) {
            throw IllegalArgumentException(
                String.format(
                    "The token was expected to have 3 parts, but got %s.",
                    parts.size
                )
            )
        }
        return parts
    }
}