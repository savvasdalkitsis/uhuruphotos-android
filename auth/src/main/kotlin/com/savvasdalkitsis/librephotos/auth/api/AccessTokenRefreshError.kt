package com.savvasdalkitsis.librephotos.auth.api

import java.io.IOException

class AccessTokenRefreshError : IOException("Could not refresh access token")