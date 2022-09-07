package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.cookies

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface CookieMonitor {

    fun monitor(context: CoroutineContext): Job
}