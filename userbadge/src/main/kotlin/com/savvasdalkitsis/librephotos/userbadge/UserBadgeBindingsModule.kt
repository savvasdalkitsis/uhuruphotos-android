package com.savvasdalkitsis.librephotos.userbadge

import com.savvasdalkitsis.librephotos.userbadge.usecase.UserBadgeUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserBadgeBindingsModule {

    @Binds
    abstract fun userBadgeUseCase(userBadgeUseCase: UserBadgeUseCase):
            com.savvasdalkitsis.librephotos.userbadge.api.UserBadgeUseCase
}