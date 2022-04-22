package com.savvasdalkitsis.librephotos.auth.service

import java.io.IOException

class AccessTokenRefreshError : IOException("Could not refresh access token")