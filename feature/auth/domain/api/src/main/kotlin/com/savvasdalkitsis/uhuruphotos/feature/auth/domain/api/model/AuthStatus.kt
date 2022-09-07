package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model

sealed class AuthStatus {

    object Unauthenticated: AuthStatus()
    object Authenticated: AuthStatus()
    object Offline: AuthStatus()
    object ServerDown: AuthStatus()

}