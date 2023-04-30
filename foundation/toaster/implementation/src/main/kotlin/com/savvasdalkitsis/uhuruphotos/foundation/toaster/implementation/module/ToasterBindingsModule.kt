package com.savvasdalkitsis.uhuruphotos.foundation.toaster.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.toaster.implementation.usecase.ToasterUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ToasterBindingsModule {

    @Binds
    abstract fun toaster(toaster: ToasterUseCase):
            com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
}