/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.module

import com.pluto.plugins.network.PlutoInterceptor
import com.savvasdalkitsis.uhuruphotos.foundation.network.api.OkHttpBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.network.implementation.BasicOkHttpClient
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
internal class OkHttpBuilder @Inject constructor(
    @BasicOkHttpClient private val okHttpBuilder: Builder,
    private val logger: HttpLoggingInterceptor.Logger,
) : OkHttpBuilder {

    override fun build(builder: Builder.() -> Builder): Builder =
        builder(okHttpBuilder)
            .addInterceptor(
                HttpLoggingInterceptor(logger).setLevel(OkHttpLogLevel.LEVEL)
            )
            .addInterceptor(
                PlutoInterceptor()
            )
}