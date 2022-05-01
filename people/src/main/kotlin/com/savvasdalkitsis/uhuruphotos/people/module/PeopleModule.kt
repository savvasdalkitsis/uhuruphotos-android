package com.savvasdalkitsis.uhuruphotos.people.module

import com.savvasdalkitsis.uhuruphotos.people.service.PeopleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class PeopleModule {

    @Provides
    fun peopleService(retrofit: Retrofit): PeopleService =
        retrofit.create(PeopleService::class.java)
}