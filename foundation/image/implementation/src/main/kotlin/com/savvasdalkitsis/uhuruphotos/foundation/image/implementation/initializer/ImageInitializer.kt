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
package com.savvasdalkitsis.uhuruphotos.foundation.image.implementation.initializer

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.AuthenticatedOkHttpClient
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.TokenRefreshInterceptor
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.model.FullImage
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import crocodile8008.videoviewcache.lib.VideoViewCacheFacade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class ImageInitializer @Inject constructor(
    @FullImage
    private val imageLoader: ImageLoader,
    @AuthenticatedOkHttpClient
    private val okHttpBuilder: OkHttpClient.Builder,
    @TokenRefreshInterceptor
    private val tokenRefreshInterceptor: Interceptor,
) : ApplicationCreated {

    override fun onAppCreated(app: Application) {
        Coil.setImageLoader { imageLoader }
        MainScope().launch(Dispatchers.IO) {
            VideoViewCacheFacade.customOkHttpClient = okHttpBuilder
                .addInterceptor(tokenRefreshInterceptor)
                .build()
        }
    }
}