package com.savvasdalkitsis.librephotos.auth.network

import com.auth0.android.jwt.JWT

val String.jwt get() = JWT(this)