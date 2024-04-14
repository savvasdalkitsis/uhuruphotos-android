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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

sealed class WelcomeMutation(
    mutation: Mutation<WelcomeState>,
) : Mutation<WelcomeState> by mutation {

    data class SetUseCases(
        val localMediaSelected: Boolean,
        val cloudMediaSelected: Boolean,
    ) : WelcomeMutation({
        it.copy(localMediaSelected = localMediaSelected, cloudMediaSelected = cloudMediaSelected)
    })

    data class SetMissingPermissions(val missingPermissions: List<String>?) : WelcomeMutation({
        it.copy(missingPermissions = missingPermissions)
    })

    data class SetLoading(val isLoading: Boolean) : WelcomeMutation({
        it.copy(isLoading = isLoading)
    })

    data object UpdateSaveButton : WelcomeMutation({
        it.copy(isSaveEnabled = it.localMediaSelected || it.cloudMediaSelected)
    })

    data class DisplayLibrePhotosHelpDialog(val showLibrePhotosHelp: Boolean) : WelcomeMutation({
        it.copy(showLibrePhotosHelp = showLibrePhotosHelp)
    })
}