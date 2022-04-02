package com.savvasdalkitsis.librephotos.module

import android.content.Context
import androidx.work.WorkManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.savvasdalkitsis.librephotos.auth.api.AuthenticationHeaderInterceptor
import com.savvasdalkitsis.librephotos.auth.api.WebLoginInterceptor
import com.savvasdalkitsis.librephotos.network.DynamicDomainInterceptor
import com.savvasdalkitsis.librephotos.web.WebkitCookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    fun okHttpBuilder(
        authenticationHeaderInterceptor: AuthenticationHeaderInterceptor,
        dynamicDomainInterceptor: DynamicDomainInterceptor,
        webLoginInterceptor: WebLoginInterceptor,
        webkitCookieManager: WebkitCookieManager,
    ): OkHttpClient.Builder = OkHttpClient().newBuilder()
        .followRedirects(false)
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .cookieJar(webkitCookieManager)
        .addInterceptor(dynamicDomainInterceptor)
        .addInterceptor(webLoginInterceptor)
        .addInterceptor(authenticationHeaderInterceptor)
//        .addInterceptor(HttpLoggingInterceptor()
//            .setLevel(HttpLoggingInterceptor.Level.BASIC)
//        )

    @Provides
    fun dateFormat(@ApplicationContext context: Context): java.text.DateFormat =
        SimpleDateFormat("yyy-MM-dd", context.resources.configuration.locale)

    @Provides
    fun workManager(@ApplicationContext context: Context): WorkManager = WorkManager
        .getInstance(context)

    @Provides
    fun androidLogAdapter(): AndroidLogAdapter = AndroidLogAdapter(
        PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .methodCount(0)
            .tag("")
            .build())
}