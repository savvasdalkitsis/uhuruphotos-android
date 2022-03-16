package com.savvasdalkitsis.librephotos.auth.usecase

import com.savvasdalkitsis.librephotos.auth.model.AuthStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationUseCase @Inject constructor() {

    fun authenticationStatus(): Flow<AuthStatus> = flow {
        emit(AuthStatus.Unauthenticated)
        delay(5000)
        emit(AuthStatus.Authenticated)
    }
}