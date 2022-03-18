package com.savvasdalkitsis.librephotos.auth.usecase

import com.savvasdalkitsis.librephotos.auth.db.dao.AuthDao
import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor(
    private val authDao: AuthDao,
) {

    fun authenticationStatus(): Flow<AuthStatus> =
        authDao.getToken()
            .onEmpty { AuthStatus.Unauthenticated }
            .map {
                when (it) {
                    null -> AuthStatus.Unauthenticated
                    else -> AuthStatus.Authenticated(it)
                }
            }

    suspend fun login(userEmail: String, password: String) {
        delay(3000)
        throw IllegalArgumentException("Not implemente")
    }
}