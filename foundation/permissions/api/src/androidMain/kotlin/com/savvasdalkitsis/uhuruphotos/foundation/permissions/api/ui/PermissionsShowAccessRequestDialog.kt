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
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state.rememberCollapsibleGroupState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
internal fun PermissionsShowAccessRequestDialog(
    state: PermissionsState,
    permissionLauncher: ActivityResultLauncher<Array<String>> = rememberPermissionFlowRequestLauncher(),
) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    YesNoDialog(
        title = stringResource(strings.missing_permissions),
        onNo = { state.showAccessRequest?.value = false },
        onYes = {
            state.showAccessRequest?.value = false
            state.missingPermissions?.let {
                permissionLauncher.launch(it.toTypedArray())
            }
        },
        yes = stringResource(strings.ok),
        no = stringResource(strings.cancel),
    ) {
        Text(stringResource(strings.need_permissions_to_manage_gallery))
        CollapsibleGroup(
            groupState = rememberCollapsibleGroupState(
                title = strings.having_problems,
                uniqueKey = "welcomeNeedsAccessSettings",
                initiallyCollapsed = true,
            )
        ) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                onClick = {
                    state.showAccessRequest?.value = false
                    navigateToSettings(navigator, context)
                }
            ) {
                Icon(
                    painter = painterResource(images.ic_settings),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(strings.navigate_to_settings))
            }
        }
        AlertText(
            text = stringResource(strings.local_media_scan_warning)
        )
    }
}

private fun navigateToSettings(navigator: Navigator?, context: Context) {
    navigator?.navigateTo(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.Builder()
                .scheme("package")
                .opaquePart(context.packageName)
                .build()
        )
    )
}

@Preview
@Composable
private fun PermissionsShowAccessRequestDialogPreview() {
    PreviewAppTheme {
        PermissionsShowAccessRequestDialog(PermissionsState(), FakeResultLauncher)
    }
}