package com.savvasdalkitsis.uhuruphotos.api.auth.model

sealed class AuthStatus {

    object Unauthenticated: AuthStatus()
    object Authenticated: AuthStatus()
    object Offline: AuthStatus()
    object ServerDown: AuthStatus()

}