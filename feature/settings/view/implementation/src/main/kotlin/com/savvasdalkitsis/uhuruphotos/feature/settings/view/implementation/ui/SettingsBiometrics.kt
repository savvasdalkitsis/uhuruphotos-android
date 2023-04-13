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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeBiometricsAppAccessRequirement
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeBiometricsHiddenPhotosAccessRequirement
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.ChangeBiometricsTrashAccessRequirement
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.EnrollToBiometrics
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions.SettingsAction
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting.Enrolled
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.BiometricsSetting.NotEnrolled
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string

@Composable
internal fun SettingsBiometrics(
    biometrics: BiometricsSetting,
    action: (SettingsAction) -> Unit,
) {
    when (biometrics) {
        NotEnrolled -> SettingsButtonRow(
            buttonText = stringResource(string.biometrics_enroll)
        ) {
            action(EnrollToBiometrics)
        }
        is Enrolled -> {
            SettingsEntryWithSubtext(
                subtext = string.changes_effect_after_restart
            ) {
                SettingsCheckBox(
                    text = stringResource(string.require_biometrics_for_app_access),
                    icon = drawable.ic_fingerprint,
                    isChecked = biometrics.requiredForAppAccess,
                    onCheckedChange = {
                        action(ChangeBiometricsAppAccessRequirement(!biometrics.requiredForAppAccess))
                    }
                )
            }
            SettingsCheckBox(
                text = stringResource(string.require_biometrics_for_hidden_photos_access),
                icon = drawable.ic_invisible,
                isChecked = biometrics.requiredForHiddenPhotosAccess,
                onCheckedChange = {
                    action(ChangeBiometricsHiddenPhotosAccessRequirement(!biometrics.requiredForHiddenPhotosAccess))
                }
            )
            SettingsCheckBox(
                text = stringResource(string.require_biometrics_for_trash_access),
                icon = drawable.ic_delete,
                isChecked = biometrics.requiredForTrashAccess,
                onCheckedChange = {
                    action(ChangeBiometricsTrashAccessRequirement(!biometrics.requiredForTrashAccess))
                }
            )
        }
    }
}