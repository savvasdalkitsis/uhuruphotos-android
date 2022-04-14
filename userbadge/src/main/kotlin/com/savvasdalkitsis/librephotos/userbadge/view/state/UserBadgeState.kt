package com.savvasdalkitsis.librephotos.userbadge.view.state

data class UserBadgeState(
    val avatarUrl: String? = null,
    val initials: String = "",
    val syncState: SyncState = SyncState.IN_PROGRESS,
)