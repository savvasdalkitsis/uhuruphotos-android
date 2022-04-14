package com.savvasdalkitsis.librephotos.auth.module

import com.savvasdalkitsis.librephotos.auth.api.model.CallErrorResponse
import com.savvasdalkitsis.librephotos.db.Database
import com.savvasdalkitsis.librephotos.token.db.TokenQueries
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CallErrorResponseAdapter

    @Provides
    @CallErrorResponseAdapter
    fun callErrorResponseAdapter(moshi: Moshi): JsonAdapter<CallErrorResponse> =
        moshi.adapter(CallErrorResponse::class.java)

    @Provides
    fun tokenQueries(database: Database): TokenQueries = database.tokenQueries
}