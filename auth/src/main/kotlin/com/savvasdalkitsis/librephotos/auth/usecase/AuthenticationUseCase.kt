package com.savvasdalkitsis.librephotos.auth.usecase

import com.savvasdalkitsis.librephotos.auth.api.AuthenticationService
import com.savvasdalkitsis.librephotos.auth.api.model.AuthenticationCredentials
import com.savvasdalkitsis.librephotos.auth.api.model.AuthenticationObtainResponse
import com.savvasdalkitsis.librephotos.auth.api.model.AuthenticationRefreshResponse
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.librephotos.auth.network.jwt
import com.savvasdalkitsis.librephotos.db.auth.Token
import com.savvasdalkitsis.librephotos.db.auth.TokenQueries
import com.savvasdalkitsis.librephotos.db.entities.auth.TokenType
import com.savvasdalkitsis.librephotos.db.extensions.awaitSingleOrNull
import com.savvasdalkitsis.librephotos.db.extensions.crud
import com.savvasdalkitsis.librephotos.infrastructure.log.log
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