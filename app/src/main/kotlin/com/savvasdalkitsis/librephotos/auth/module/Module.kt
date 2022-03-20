package com.savvasdalkitsis.librephotos.auth.module

import com.savvasdalkitsis.librephotos.auth.api.model.CallErrorResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CallErrorResponseAdapter

    @Provides
    @CallErrorResponseAdapter
    fun callErrorResponseAdapter(moshi: Moshi): JsonAdapter<CallErrorResponse> =
        moshi.adapter(CallErrorResponse::class.java)
}