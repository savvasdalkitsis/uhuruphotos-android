package com.savvasdalkitsis.librephotos.infrastructure.extensions

import android.webkit.URLUtil

val String.isValidUrl get() = URLUtil.isValidUrl(this)

fun Double.round(decimals: Int = 2): String = "%.${decimals}f".format(this)