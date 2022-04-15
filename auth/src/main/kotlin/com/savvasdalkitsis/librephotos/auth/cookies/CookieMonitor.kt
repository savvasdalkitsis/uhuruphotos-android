package com.savvasdalkitsis.librephotos.auth.cookies

import android.webkit.CookieManager
import com.savvasdalkitsis.librephotos.auth.usecase.ServerUseCase
import com.savvasdalkitsis.librephotos.log.log
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class CookieMonitor @Inject constructor(
    private val serverUseCase: ServerUseCase,
) {

    fun monitor(context: CoroutineContext): Job = CoroutineScope(Dispatchers.IO + context).launch {
        val cookies = CookieManager.getInstance()
        val server = serverUseCase.getServerUrl()
        var cookie: String?
        do {
            cookie = cookies.getCookie(server)
            log { "Cookie was: $cookie" }
            delay(500)
        } while (cookie?.contains("_forward_auth=") == false)
    }
}