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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.usecase.AvatarUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.search.domain.api.usecase.SearchUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.CacheSettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.SystemUseCase
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class SettingsActionsContext @Inject constructor(
    val settingsUseCase: SettingsUseCase,
    val feedWorkScheduler: FeedWorkScheduler,
    val avatarUseCase: AvatarUseCase,
    val cacheUseCase: CacheSettingsUseCase,
    val feedbackUseCase: FeedbackUseCase,
    val searchUseCase: SearchUseCase,
    val biometricsUseCase: BiometricsUseCase,
    val systemUseCase: SystemUseCase,
) {

    fun authenticateIfNeeded(
        required: Boolean,
        change: suspend (Boolean) -> Unit,
    ) = flow<SettingsMutation> {
        val proceed = when {
            required -> Result.success(Unit)
            else -> biometricsUseCase.authenticate(
                string.authenticate,
                string.authenticate_to_change,
                string.authenticate_to_change_description,
                true,
            )
        }
        if (proceed.isSuccess) {
            change(required)
        }
    }
}
