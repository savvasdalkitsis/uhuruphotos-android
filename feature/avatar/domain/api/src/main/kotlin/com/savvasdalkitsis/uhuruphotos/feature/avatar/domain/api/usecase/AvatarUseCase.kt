package com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase

import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import kotlinx.coroutines.flow.Flow

interface AvatarUseCase {
    fun getAvatarState(): Flow<AvatarState>
}