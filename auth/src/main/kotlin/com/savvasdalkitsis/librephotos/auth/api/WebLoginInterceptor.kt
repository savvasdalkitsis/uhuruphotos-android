package com.savvasdalkitsis.librephotos.auth.api

import android.webkit.CookieManager
import com.savvasdalkitsis.librephotos.navigation.ControllersProvider
import com.savvasdalkitsis.librephotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.auth.weblogin.weblogin.navigation.WebLoginNavigationTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class WebLoginInterceptor @Inject constructor(
    private val controllersProvider: ControllersProvider,
    private val serverUseCase: ServerUseCase,
    private val cookieManager: CookieManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code == 307) {
            CoroutineScope(Dispatchers.Main).launch {
                cookieManager.setCookie(serverUseCase.getServerUrl(), "")
                controllersProvider.navController?.navigate(
                    WebLoginNavigationTarget.name(response.header("Location")!!)
                )
            }
        }
        return response
    }

}