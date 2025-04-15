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
package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.usecase

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import com.savvasdalkitsis.uhuruphotos.foundation.activity.api.holder.CurrentActivityHolder
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics.Enrolled
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics.NoHardware
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics.NotEnrolled
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.authenticate
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.implementation.assure.Prompt
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.awaitOnMain
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.runCatchingWithLog
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.simple
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@AutoBind
@ActivityRetainedScoped
internal class BiometricsUseCase @Inject constructor(
    private val biometricManager: BiometricManager,
    private val currentActivityHolder: CurrentActivityHolder,
) : BiometricsUseCase {

    override fun observeBiometrics(): Flow<Biometrics> = flow {
        do {
            emit(getBiometrics())
            delay(2000)
        } while (currentCoroutineContext().isActive)
    }.distinctUntilChanged()

    override fun getBiometrics(): Biometrics = when (biometricManager.canAuthenticate()) {
        BIOMETRIC_SUCCESS -> Enrolled
        BIOMETRIC_ERROR_NO_HARDWARE -> NoHardware
        else -> NotEnrolled
    }

    override suspend fun authenticate(
        title: StringResource,
        subtitle: StringResource,
        description: StringResource,
        confirmRequired: Boolean,
    ): SimpleResult = runCatchingWithLog {
        with(currentActivityHolder.currentActivity!!) {
            awaitOnMain {
                val t = getString(title)
                val s = getString(subtitle)
                val d = getString(description)
                suspendCancellableCoroutine { continuation ->
                    authenticate(
                        Prompt(
                            title = t,
                            subtitle = s,
                            description = d,
                            confirmRequired = confirmRequired,
                            deviceCredentialsAllowed = true,
                        )
                    ) { exception ->
                        if (continuation.isActive) {
                            when (exception) {
                                null -> continuation.resume(Unit)
                                else -> continuation.resumeWithException(exception)
                            }
                        }
                    }
                }
            }
        }
    }.simple()
}