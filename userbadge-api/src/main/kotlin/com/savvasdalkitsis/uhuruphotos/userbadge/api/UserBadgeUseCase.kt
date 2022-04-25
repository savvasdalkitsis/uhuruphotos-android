package com.savvasdalkitsis.uhuruphotos.userbadge.api

import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState
import kotlinx.coroutines.flow.Flow

interface UserBadgeUseCase {
    fun getUserBadgeState(): Flow<UserInformationState>
}