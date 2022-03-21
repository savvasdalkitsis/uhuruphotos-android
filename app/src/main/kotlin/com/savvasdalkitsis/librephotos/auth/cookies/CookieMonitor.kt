package com.savvasdalkitsis.librephotos.auth.cookies

import android.webkit.CookieManager
import com.savvasdalkitsis.librephotos.auth.usecase.AuthenticationUseCase
import com.savvasdalkitsis.librephotos.server.usecase.ServerUseCase
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CookieMonitor @Inject constructor(
    val serverUseCase: ServerUseCase,
    val authenticationUseCase: AuthenticationUseCase,
) {

    fun monitor(): Job = CoroutineScope(Dispatchers.IO).launch {
        val cookies = CookieManager.getInstance()
        val server = serverUseCase.getServerUrl()
        var cookie: String?
        do {
            cookie = cookies.getCookie(server)
            Timber.w(cookie)
            delay(500)
        } while (cookie?.contains("_forward_auth=") == false)
    }
}
//uQYwQ6rJAodqBZC2x7tmex