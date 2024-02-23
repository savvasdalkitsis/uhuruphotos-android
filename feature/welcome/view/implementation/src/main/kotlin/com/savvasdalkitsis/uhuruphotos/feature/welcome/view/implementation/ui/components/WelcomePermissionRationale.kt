/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.HidePermissionRationale
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.MultiButtonDialog

@Composable
internal fun WelcomePermissionRationale(
    action: (WelcomeAction) -> Unit,
    state: WelcomeState,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>?
) {
    MultiButtonDialog(
        title = stringResource(R.string.missing_permissions),
        onDismiss = { action(HidePermissionRationale) },
        confirmButton = {
            Button(onClick = {
                action(HidePermissionRationale)
                state.missingPermissions?.let {
                    permissionLauncher?.launch(it.toTypedArray())
                }
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        negativeButtonText = stringResource(R.string.cancel)
    ) {
        Text(stringResource(R.string.need_permissions_to_manage_gallery))
    }
}