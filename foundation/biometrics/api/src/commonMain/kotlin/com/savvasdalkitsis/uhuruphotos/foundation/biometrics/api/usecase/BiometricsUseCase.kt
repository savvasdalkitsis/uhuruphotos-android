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
package com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase

import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.model.Biometrics
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource

interface BiometricsUseCase {
    fun getBiometrics(): Biometrics
    suspend fun authenticate(
        title: StringResource,
        subtitle: StringResource,
        description: StringResource,
        confirmRequired: Boolean,
    ): SimpleResult

    fun observeBiometrics(): Flow<Biometrics>
}