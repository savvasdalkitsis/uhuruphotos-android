package com.savvasdalkitsis.librephotos.user.module

import com.savvasdalkitsis.librephotos.db.Database
import com.savvasdalkitsis.librephotos.user.UserQueries
import com.savvasdalkitsis.librephotos.user.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun userApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    fun userQueries(database: Database): UserQueries = database.userQueries

}