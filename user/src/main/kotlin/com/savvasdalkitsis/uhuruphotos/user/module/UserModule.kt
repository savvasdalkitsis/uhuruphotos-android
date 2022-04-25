package com.savvasdalkitsis.uhuruphotos.user.module

import com.savvasdalkitsis.uhuruphotos.db.Database
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.user.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Provides
    fun userApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    fun userQueries(database: Database): UserQueries = database.userQueries

}