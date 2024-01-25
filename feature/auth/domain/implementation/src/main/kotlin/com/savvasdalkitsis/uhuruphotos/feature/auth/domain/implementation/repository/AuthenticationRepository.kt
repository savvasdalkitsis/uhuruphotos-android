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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase.JwtUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.Token
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.PlainTextPreferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.get
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import javax.inject.Inject

internal const val EXPIRED = "EXPIRED"

class AuthenticationRepository @Inject constructor(
    private val tokenQueries: TokenQueries,
    private val jwtUseCase: JwtUseCase,
    @PlainTextPreferences
    private val preferences: Preferences,
) {
    private val userKey = "userIdFromToken"

    fun clearTokensForAuthenticationLoss() {
        tokenQueries.delete(TokenType.ACCESS)
        tokenQueries.saveToken(Token(
            token = EXPIRED,
            type = TokenType.REFRESH,
        ))
    }

    fun saveAccessToken(token: String) {
        setUserIdFromToken(token)
        tokenQueries.saveToken(
            Token(
                token = token,
                type = TokenType.ACCESS
            )
        )
    }

    fun saveRefreshToken(token: String) {
        tokenQueries.saveToken(
            Token(
                token = token,
                type = TokenType.REFRESH,
            )
        )
    }

    fun setUserIdFromToken(token: String) {
        preferences.set(userKey, token.userId)
    }

    fun getUserId() = preferences.get<String?>(userKey, null)

    private val String.userId: String get() = with(jwtUseCase) {
        return this@userId["user_id"]
    }
}