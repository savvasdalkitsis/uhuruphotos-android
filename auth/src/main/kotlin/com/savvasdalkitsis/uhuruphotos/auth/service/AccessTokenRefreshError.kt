package com.savvasdalkitsis.uhuruphotos.auth.service

import java.io.IOException

class AccessTokenRefreshError : IOException("Could not refresh access token")