package com.savvasdalkitsis.librephotos.module

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.savvasdalkitsis.librephotos.auth.api.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.librephotos.auth.api.AuthenticationService
import com.savvasdalkitsis.librephotos.auth.api.TokenRefreshInterceptor
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.db.LibrePhotosDatabase
import com.savvasdalkitsis.librephotos.network.DynamicDomainInterceptor
import com.savvasdalkitsis.librephotos.photos.api.PhotosService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SingletonModule {

    @Provides
    @Singleton
    fun authDao(db: LibrePhotosDatabase) = db.authDao()

    @Provides
    @Singleton
    fun serverDao(db: LibrePhotosDatabase) = db.serverDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): LibrePhotosDatabase {
        return Room.databaseBuilder(
            appContext,
            LibrePhotosDatabase::class.java,
            "LibrePhotos"
        ).build()
    }

    @Provides
    @Singleton
    fun authenticationService(
        @AuthenticationRetrofit retrofit: Retrofit,
    ): AuthenticationService = retrofit.create(AuthenticationService::class.java)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthenticationRetrofit

    @Provides
    @Singleton
    @AuthenticationRetrofit
    fun authenticationRetrofit(
        retrofitBuilder: Retrofit.Builder,
        okHttpBuilder: OkHttpClient.Builder,
    ): Retrofit = retrofitBuilder
        .client(okHttpBuilder
            .build())
        .build()

    @Provides
    @Singleton
    fun retrofit(
        retrofitBuilder: Retrofit.Builder,
        okHttpBuilder: OkHttpClient.Builder,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
    ): Retrofit = retrofitBuilder
        .client(okHttpBuilder
            .addInterceptor(tokenRefreshInterceptor)
            .build())
        .build()

    @Provides
    @Singleton
    fun retrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl("https://localhost/")
        .addConverterFactory(MoshiConverterFactory.create())

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
            .build()
}