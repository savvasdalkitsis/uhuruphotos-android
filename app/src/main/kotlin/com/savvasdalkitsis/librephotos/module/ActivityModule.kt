package com.savvasdalkitsis.librephotos.module

import android.content.Context
import android.os.Build
import androidx.navigation.NavController
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.savvasdalkitsis.librephotos.auth.api.TokenRefreshInterceptor
import com.savvasdalkitsis.librephotos.navigation.NavControllerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {

    @Provides
    fun coilImageLoader(
        @ActivityContext context: Context,
        @SingletonModule.ImageCache imageCache: Cache,
        okHttpClientBuilder: OkHttpClient.Builder,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
    ) =
        ImageLoader.Builder(context)
            .okHttpClient(okHttpClientBuilder
                .cache(imageCache)
                .addInterceptor(tokenRefreshInterceptor)
                .build())
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder(context))
                } else {
                    add(GifDecoder())
                }
            }
            .build()
}