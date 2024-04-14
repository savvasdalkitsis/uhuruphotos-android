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

@Parcelize
data class ServerState(
    val isLoading: Boolean = false,
    val isLoggingEnabled: Boolean = false,
    val prefilledUrl: String = "",
    val currentUrl: String = "",
    val isUrlValid: Boolean = false,
    val showUnsecureServerConfirmation: Boolean = false,
    val showHelpDialog: Boolean = false,
    val username: String = "",
    @Redacted val password: String = "",
    val allowLogin: Boolean = false,
    val rememberCredentials: Boolean = false,
    val passwordVisible: Boolean = false,
) : Parcelable