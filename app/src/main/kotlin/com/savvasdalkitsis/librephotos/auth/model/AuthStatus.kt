package com.savvasdalkitsis.librephotos.auth.model

sealed class AuthStatus {

    object Unauthenticated: AuthStatus()
    object Authenticated: AuthStatus()

}
