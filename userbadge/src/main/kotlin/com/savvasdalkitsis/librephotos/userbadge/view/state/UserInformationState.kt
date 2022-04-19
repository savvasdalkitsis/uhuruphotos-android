package com.savvasdalkitsis.librephotos.userbadge.view.state

data class UserInformationState(
    val avatarUrl: String? = null,
    val initials: String = "",
    val userFullName: String = "",
    val serverUrl: String = "",
    val syncState: SyncState = SyncState.IN_PROGRESS,
)