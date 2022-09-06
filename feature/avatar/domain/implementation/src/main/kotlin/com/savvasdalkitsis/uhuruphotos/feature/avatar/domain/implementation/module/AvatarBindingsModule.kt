package com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.implementation.usecase.AvatarUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AvatarBindingsModule {

    @Binds
    abstract fun avatarUseCase(avatarUseCase: AvatarUseCase):
            com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase.AvatarUseCase
}