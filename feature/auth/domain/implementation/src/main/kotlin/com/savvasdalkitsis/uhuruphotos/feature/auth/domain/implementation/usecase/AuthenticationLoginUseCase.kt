/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.model.AuthStatus.Authenticated
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationLoginUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.Credentials
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.repository.AuthenticationRepository
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.http.AuthenticationService
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.service.http.request.toAuthenticationRequestData
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.Database
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.async
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.clearAllUserLinkedTables
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.usecase.ImageCacheUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.lang.api.letBoth
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.video.api.usecase.VideoCacheUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
class AuthenticationLoginUseCase @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val authenticationRepository: AuthenticationRepository,
    private val db: Database,
    private val imageCacheUseCase: ImageCacheUseCase,
    private val videoCacheUseCase: VideoCacheUseCase,
    private val workScheduleUseCase: WorkScheduleUseCase,
) : AuthenticationLoginUseCase {

    override suspend fun login(
        credentials: Credentials,
        rememberCredentials: Boolean,
    ): Result<AuthStatus, Throwable> = runCatchingWithLog {
        val response = authenticationService.login(credentials.toAuthenticationRequestData)
        with(authenticationRepository) {
            if (getUserId() != response.access.userId) {
                logOut()
                clearCredentials()
            }
            saveRefreshToken(response.refresh)
            saveAccessToken(response.access)
            if (rememberCredentials) {
                saveUsername(credentials.username)
                savePassword(credentials.password)
            } else {
                clearCredentials()
            }
        }
        return Ok(Authenticated)
    }

    override suspend fun logOut() = async {
        workScheduleUseCase.cancelAllScheduledWork()
        db.clearAllUserLinkedTables()
        imageCacheUseCase.clearAll()
        videoCacheUseCase.clearAll()
    }

    override suspend fun loadSavedCredentials(): Credentials? = with(authenticationRepository) {
        letBoth(getUsername(), getPassword()) { username, password ->
            Credentials(username, password)
        }
    }

    context(AuthenticationRepository)
    private fun clearCredentials() {
        clearUsername()
        clearPassword()
    }
}
