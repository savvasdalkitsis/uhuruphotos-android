package com.savvasdalkitsis.librephotos.server.view

import android.os.Parcelable
import dev.zacsweers.redacted.annotations.Redacted
import kotlinx.parcelize.Parcelize

sealed class ServerState : Parcelable {
    @Parcelize
    object Loading: ServerState()
    @Parcelize
    data class ServerUrl(
        val url: String,
        val isUrlValid: Boolean,
        val allowSaveUrl: Boolean,
    ): ServerState(), Parcelable
    @Parcelize
    data class UserCredentials(
        val username: String,
        @Redacted val password: String,
        val allowLogin: Boolean,
    ): ServerState(), Parcelable
}
