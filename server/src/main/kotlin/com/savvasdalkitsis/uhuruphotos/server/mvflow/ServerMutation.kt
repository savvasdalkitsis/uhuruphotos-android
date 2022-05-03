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
package com.savvasdalkitsis.uhuruphotos.server.mvflow

import dev.zacsweers.redacted.annotations.Redacted

sealed class ServerMutation {

    data class AskForServerDetails(val previousUrl: String?, val isValid: Boolean) : ServerMutation()
    data class AskForUserCredentials(val userName: String, @Redacted val password: String) : ServerMutation()
    object PerformingBackgroundJob : ServerMutation()
    data class ShowUrlValidation(val prefilledUrl: String?, val isValid: Boolean) : ServerMutation()
    data class ChangeUsernameTo(val username: String) : ServerMutation()
    data class ChangePasswordTo(@Redacted val password: String) : ServerMutation()
}
