package com.savvasdalkitsis.uhuruphotos.auth.usecase

import com.savvasdalkitsis.uhuruphotos.auth.service.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationCredentials
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationObtainResponse
import com.savvasdalkitsis.uhuruphotos.auth.service.model.AuthenticationRefreshResponse
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.uhuruphotos.auth.network.jwt
import com.savvasdalkitsis.uhuruphotos.db.auth.Token
import com.savvasdalkitsis.uhuruphotos.db.auth.TokenQueries
import com.savvasdalkitsis.uhuruphotos.db.entities.auth.TokenType
import com.savvasdalkitsis.uhuruphotos.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.log.log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        crud {
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
            } catch (e: Exception) {
                log(e)
                Unauthenticated
            }
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String): AuthenticationRefreshResponse {
        val response = authenticationService.refreshToken(AuthenticationObtainResponse(refreshToken))
        crud {
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