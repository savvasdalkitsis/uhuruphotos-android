package com.savvasdalkitsis.librephotos.auth.model

sealed class AuthStatus {

    object Unauthenticated: AuthStatus()
    data class Authenticated(val token: String): AuthStatus()

}
