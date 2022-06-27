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
package com.savvasdalkitsis.uhuruphotos.implementation.settings.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.icons.R
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.ChangeBiometricsAppAccessRequirement
import com.savvasdalkitsis.uhuruphotos.implementation.settings.seam.SettingsAction.EnrollToBiometrics
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.BiometricsSetting.Enrolled
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.BiometricsSetting.NotEnrolled
import com.savvasdalkitsis.uhuruphotos.implementation.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.api.strings.R as Strings

@Composable
internal fun SettingsGroupBiometrics(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
    collapsed: MutableState<Boolean> = remember { mutableStateOf(false) },
) {
    state.biometrics?.let { biometrics ->
        SettingsGroup(
            title = stringResource(Strings.string.biometrics),
            collapsed = collapsed,
        ) {
            when (biometrics) {
                NotEnrolled -> SettingsButtonRow(
                    buttonText = stringResource(Strings.string.biometrics_enroll)
                ) {
                    action(EnrollToBiometrics)
                }
                is Enrolled -> SettingsEntryWithSubtext(
                    subtext = Strings.string.changes_effect_after_restart
                ) {
                    SettingsCheckBox(
                        text = stringResource(Strings.string.require_biometrics_for_app_access),
                        icon = R.drawable.ic_fingerprint,
                        isChecked = biometrics.requiredForAppAccess,
                        onCheckedChange = {
                            action(ChangeBiometricsAppAccessRequirement(!biometrics.requiredForAppAccess))
                        }
                    )
                }
            }
        }
    }
}