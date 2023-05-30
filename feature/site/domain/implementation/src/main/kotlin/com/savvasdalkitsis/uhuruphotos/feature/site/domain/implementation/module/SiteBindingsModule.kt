package com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.usecase.SiteUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SiteBindingsModule {

    @Binds
    abstract fun siteUseCase(useCase: SiteUseCase):
            com.savvasdalkitsis.uhuruphotos.feature.site.domain.api.usecase.SiteUseCase

}