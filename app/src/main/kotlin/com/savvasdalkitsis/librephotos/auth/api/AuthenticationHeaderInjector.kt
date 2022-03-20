package com.savvasdalkitsis.librephotos.auth.api

import com.savvasdalkitsis.librephotos.auth.db.dao.AuthDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationHeaderInjector @Inject constructor(
    private val authDao: AuthDao,
) {

    fun inject(chain: Interceptor.Chain): Request {
        val accessToken = runBlocking { authDao.getAccessToken() }
        return chain.request().newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .header("Cookie", "jwt=$accessToken")
            .build()
    }

}
