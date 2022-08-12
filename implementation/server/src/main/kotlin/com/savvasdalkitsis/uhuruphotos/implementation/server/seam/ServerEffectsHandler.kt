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
package com.savvasdalkitsis.uhuruphotos.implementation.server.seam

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.usecase.FeedbackUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect.Close
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect.ErrorLoggingIn
import com.savvasdalkitsis.uhuruphotos.implementation.server.seam.ServerEffect.SendFeedback
import javax.inject.Inject

internal class ServerEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
    private val feedbackUseCase: FeedbackUseCase,
) : EffectHandler<ServerEffect> {

    override suspend fun handleEffect(effect: ServerEffect) {
        when (effect) {
            Close -> navigator.navigateBack()
            is ErrorLoggingIn -> toaster.show(string.error_logging_in)
            SendFeedback -> feedbackUseCase.sendFeedback()
        }
    }
}