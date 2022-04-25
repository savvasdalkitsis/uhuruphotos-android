package com.savvasdalkitsis.uhuruphotos.auth.model

sealed class AuthStatus {

    object Unauthenticated: AuthStatus()
    object Authenticated: AuthStatus()

}
