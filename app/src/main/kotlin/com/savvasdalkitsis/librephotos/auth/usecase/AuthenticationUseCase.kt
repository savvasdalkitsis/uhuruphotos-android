package com.savvasdalkitsis.librephotos.auth.usecase

import com.savvasdalkitsis.librephotos.auth.api.AuthenticationService
import com.savvasdalkitsis.librephotos.auth.api.model.AuthenticationCredentials
import com.savvasdalkitsis.librephotos.auth.api.model.AuthenticationObtainResponse
import com.savvasdalkitsis.librephotos.auth.api.model.AuthenticationRefreshResponse
import com.savvasdalkitsis.librephotos.auth.db.dao.AuthDao
import com.savvasdalkitsis.librephotos.auth.db.entities.Token
import com.savvasdalkitsis.librephotos.auth.db.entities.TokenType
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Authenticated
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus.Unauthenticated
import com.savvasdalkitsis.librephotos.network.jwt
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor(
    private val authDao: AuthDao,
    private val authenticationService: AuthenticationService,
) {

    private val mutex = Mutex()

    suspend fun authenticationStatus(): AuthStatus {
        val accessToken = authDao.getAccessToken()
        return when {
            accessToken.isNullOrEmpty() || accessToken.jwt.isExpired(0) -> refreshToken()
            else -> Authenticated
        }
    }

    suspend fun login(username: String, password: String) {
        val response = authenticationService.login(AuthenticationCredentials(username, password))
        authDao.saveToken(Token(
            token = response.refresh,
            type = TokenType.REFRESH,
        ))
        refreshAccessToken(response.refresh)
    }

    suspend fun refreshToken(): AuthStatus = mutex.withLock {
        val refreshToken = authDao.getRefreshToken()
        return when {
            refreshToken.isNullOrEmpty() || refreshToken.jwt.isExpired(0) -> Unauthenticated
            else -> try {
                refreshAccessToken(refreshToken)
                Authenticated
            } catch (e: Exception) {
                Timber.w(e)
                Unauthenticated
            }
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String): AuthenticationRefreshResponse {
        val response = authenticationService.refreshToken(AuthenticationObtainResponse(refreshToken))
        authDao.saveToken(
            Token(
                token = response.access,
                type = TokenType.ACCESS
            )
        )
        return response
    }
}