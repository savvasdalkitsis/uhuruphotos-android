package com.savvasdalkitsis.uhuruphotos.foundation.system.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.system.implementation.usecase.SystemUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemBindingsModule {

    @Binds
    abstract fun systemUseCase(systemUseCase: SystemUseCase):
            com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.SystemUseCase
}