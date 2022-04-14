package com.savvasdalkitsis.librephotos.module

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.PrettyFormatStrategy
import com.savvasdalkitsis.librephotos.server.network.DynamicDomainInterceptor
import com.savvasdalkitsis.librephotos.auth.weblogin.WebkitCookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import java.text.DateFormat
import java.text.DateFormat.FULL
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class Module {

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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParsingDateFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParsingDateTimeFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingDateFormat
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DisplayingDateTimeFormat

    @SuppressLint("SimpleDateFormat")
    @Provides
    @ParsingDateFormat
    fun parsingDateFormat(): DateFormat = SimpleDateFormat("yyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    @Provides
    @ParsingDateTimeFormat
    fun parsingDateTimeFormat(): DateFormat = SimpleDateFormat("yyy-MM-dd'T'kk:mm:ss'Z'")

    @Provides
    @DisplayingDateFormat
    fun displayingDateFormat(): DateFormat = DateFormat.getDateInstance(FULL)

    @Provides
    @DisplayingDateTimeFormat
    fun displayingDateTimeFormat(): DateFormat = DateFormat.getDateTimeInstance(FULL, FULL)

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