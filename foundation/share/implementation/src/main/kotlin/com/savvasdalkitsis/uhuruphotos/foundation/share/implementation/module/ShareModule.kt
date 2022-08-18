package com.savvasdalkitsis.uhuruphotos.foundation.share.implementation.module

import com.savvasdalkitsis.uhuruphotos.foundation.share.implementation.usecase.ShareUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class ShareModule {

    @Binds
    abstract fun shareUseCase(shareUseCase: ShareUseCase):
            com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase

}