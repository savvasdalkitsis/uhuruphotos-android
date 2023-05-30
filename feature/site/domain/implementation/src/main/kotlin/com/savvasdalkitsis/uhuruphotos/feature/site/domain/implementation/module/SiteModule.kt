package com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.site.domain.implementation.service.SiteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SiteModule {

    @Provides
    fun siteService(retrofit: Retrofit): SiteService = retrofit.create(SiteService::class.java)

}