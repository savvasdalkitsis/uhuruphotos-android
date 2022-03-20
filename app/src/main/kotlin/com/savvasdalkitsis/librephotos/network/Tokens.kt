package com.savvasdalkitsis.librephotos.network

import com.auth0.android.jwt.JWT

val String.jwt get() = JWT(this)