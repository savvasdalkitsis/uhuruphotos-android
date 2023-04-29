package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.LocalMediaDeletionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class LocalMediaActivityBindingsModule {

    @Binds
    abstract fun localMediaDeletionUseCase(useCase: LocalMediaDeletionUseCase):
            com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaDeletionUseCase
}