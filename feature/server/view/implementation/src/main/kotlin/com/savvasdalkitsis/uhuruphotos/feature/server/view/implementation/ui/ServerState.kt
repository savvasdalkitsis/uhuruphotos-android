/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui

import android.os.Parcelable
import dev.zacsweers.redacted.annotations.Redacted
import kotlinx.parcelize.Parcelize

internal sealed class ServerState(
    open val isLoggingEnabled: Boolean = false,
) : Parcelable {
    @Parcelize
    data class Loading(
        override val isLoggingEnabled: Boolean,
    ): ServerState(isLoggingEnabled)
    @Parcelize
    data class ServerUrl(
        val prefilledUrl: String,
        val isUrlValid: Boolean,
        val allowSaveUrl: Boolean,
        override val isLoggingEnabled: Boolean,
        val showUnsecureServerConfirmation: Boolean = false,
    ): ServerState(isLoggingEnabled), Parcelable
    @Parcelize
    data class UserCredentials(
        val username: String,
        @Redacted val password: String,
        override val isLoggingEnabled: Boolean,
        val allowLogin: Boolean,
        val passwordVisible: Boolean,
    ): ServerState(isLoggingEnabled), Parcelable
}
