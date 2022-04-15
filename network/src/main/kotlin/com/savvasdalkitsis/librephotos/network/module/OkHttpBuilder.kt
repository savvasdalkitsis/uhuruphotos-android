package com.savvasdalkitsis.librephotos.network.module

import com.savvasdalkitsis.librephotos.network.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class OkHttpBuilder @Inject constructor(
    @NetworkModule.BasicOkHttpClient private val okHttpBuilder: OkHttpClient.Builder,
) {

    fun build(builder: OkHttpClient.Builder.() -> OkHttpClient.Builder): OkHttpClient.Builder =
        builder(okHttpBuilder)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                }
            }
}