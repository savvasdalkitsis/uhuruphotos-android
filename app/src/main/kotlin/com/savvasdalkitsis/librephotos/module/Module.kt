package com.savvasdalkitsis.librephotos.module

import android.content.Context
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.savvasdalkitsis.librephotos.auth.weblogin.WebkitCookieManager
import com.savvasdalkitsis.librephotos.feedpage.navigation.FeedPageNavigationTarget
import com.savvasdalkitsis.librephotos.server.navigation.ServerNavigationTarget
import com.savvasdalkitsis.librephotos.server.network.DynamicDomainInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Provides
    @com.savvasdalkitsis.librephotos.home.module.Module.HomeNavigationTargetFeed
    fun homeNavigationTargetFeed(): String = FeedPageNavigationTarget.name

    @Provides
    @com.savvasdalkitsis.librephotos.home.module.Module.HomeNavigationTargetSearch
    fun homeNavigationTargetSearch(): String = ServerNavigationTarget.name

    @Provides
    fun okHttpBuilder(
        authenticationHeaderInterceptor: com.savvasdalkitsis.librephotos.auth.api.AuthenticationHeaderInterceptor,
        dynamicDomainInterceptor: DynamicDomainInterceptor,
        webLoginInterceptor: com.savvasdalkitsis.librephotos.auth.api.WebLoginInterceptor,
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
    fun workManager(@ApplicationContext context: Context): WorkManager = WorkManager
        .getInstance(context)

    @Provides
    fun androidLogAdapter(): AndroidLogAdapter = AndroidLogAdapter(
        PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .methodCount(0)
            .tag("")
            .build())

    @ExperimentalCoroutinesApi
    @Provides
    fun preferences(@ApplicationContext context: Context): FlowSharedPreferences =
        FlowSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context))

    @Provides
    fun packageManager(@ApplicationContext context: Context): PackageManager = context.packageManager
}