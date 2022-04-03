package com.savvasdalkitsis.librephotos.extensions

import android.webkit.URLUtil

val String.isValidUrl get() = URLUtil.isValidUrl(this)