/*
Copyright 2024 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.foundation.video.api.module

import android.annotation.SuppressLint
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.AuthModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.ExoplayerProvider
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.module.cache.VideoCacheModule
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@SuppressLint("UnsafeOptInUsageError")
object VideoModule {

    val exoPlayerProvider: ExoplayerProvider by singleInstance {
        com.savvasdalkitsis.uhuruphotos.foundation.video.implementation.ExoplayerProvider(
            AndroidModule.applicationContext, cacheDataSourceFactory
        )
    }

    private val cacheDataSourceFactory: CacheDataSource.Factory by singleInstance {
        CacheDataSource.Factory()
            .setCache(VideoCacheModule.videoCache)
            .setUpstreamDataSourceFactory(OkHttpDataSource.Factory(videoOkHttpClient))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private val videoOkHttpClient: OkHttpClient by singleInstance {
        AuthModule.authenticatedOkHttpBuilder
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
            .addInterceptor(AuthModule.tokenRefreshInterceptor)
            .build()
    }
}