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
package com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.MultiButtonDialog
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
internal fun PermissionsCheck(
    state: PermissionsState,
) {
    state.Compose()
    val permissionLauncher = rememberPermissionFlowRequestLauncher()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    if (state.showRationale.value) {
        MultiButtonDialog(
            title = stringResource(R.string.missing_permissions),
            onDismiss = { state.showRationale.value = false },
            confirmButton = {
                Button(onClick = {
                    state.showRationale.value = false
                    state.missingPermissions?.let {
                        permissionLauncher.launch(it.toTypedArray())
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
    if (state.showAccessRequest.value) {
        MultiButtonDialog(
            title = stringResource(R.string.missing_permissions),
            onDismiss = { state.showAccessRequest.value = false },
            extraButtons = listOf {
                OutlinedButton(onClick = {
                    state.showAccessRequest.value = false
                    navigateToSettings(navigator, context)
                }) {
                    Text(stringResource(R.string.navigate_to_settings))
                }
            },
            confirmButton = {
                Button(onClick = {
                    state.showAccessRequest.value = false
                    state.missingPermissions?.let {
                        permissionLauncher.launch(it.toTypedArray())
                    }
                }) {
                    Text(stringResource(R.string.attempt_grant_permissions))
                }
            },
            negativeButtonText = stringResource(R.string.cancel)
        ) {
            Text(stringResource(R.string.need_permissions_to_manage_gallery))
        }
    }
}

private fun navigateToSettings(navigator: Navigator?, context: Context) {
    navigator?.navigateTo(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.Builder()
        .scheme("package")
        .opaquePart(context.packageName)
        .build())
    )
}
