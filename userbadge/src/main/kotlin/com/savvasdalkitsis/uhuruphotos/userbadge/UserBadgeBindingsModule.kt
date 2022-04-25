package com.savvasdalkitsis.uhuruphotos.userbadge

import com.savvasdalkitsis.uhuruphotos.userbadge.usecase.UserBadgeUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserBadgeBindingsModule {

    @Binds
    abstract fun userBadgeUseCase(userBadgeUseCase: UserBadgeUseCase):
            com.savvasdalkitsis.uhuruphotos.userbadge.api.UserBadgeUseCase
}