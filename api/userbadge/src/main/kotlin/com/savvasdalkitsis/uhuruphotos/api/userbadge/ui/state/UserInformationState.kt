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
package com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state

data class UserInformationState(
    val avatarUrl: String? = null,
    val initials: String = "",
    val userFullName: String = "",
    val serverUrl: String = "",
    val syncState: SyncState = SyncState.IN_PROGRESS,
)

val previewUserInformationState = UserInformationState(
    initials = "SD",
    userFullName = "Savvas Dalkitsis",
    serverUrl = "https://librephotos.server.url",
    syncState = SyncState.GOOD,
)