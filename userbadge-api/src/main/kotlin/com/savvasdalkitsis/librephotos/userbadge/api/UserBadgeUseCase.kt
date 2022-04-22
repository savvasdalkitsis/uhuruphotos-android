package com.savvasdalkitsis.librephotos.userbadge.api

import com.savvasdalkitsis.librephotos.userbadge.api.view.state.UserInformationState
import kotlinx.coroutines.flow.Flow

interface UserBadgeUseCase {
    fun getUserBadgeState(): Flow<UserInformationState>
}