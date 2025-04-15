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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.LocalNavigator
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.UhuruCollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state.rememberCollapsibleGroupState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.cancel
import uhuruphotos_android.foundation.strings.api.generated.resources.having_problems
import uhuruphotos_android.foundation.strings.api.generated.resources.local_media_scan_warning
import uhuruphotos_android.foundation.strings.api.generated.resources.missing_permissions
import uhuruphotos_android.foundation.strings.api.generated.resources.navigate_to_settings
import uhuruphotos_android.foundation.strings.api.generated.resources.need_permissions_to_manage_gallery
import uhuruphotos_android.foundation.strings.api.generated.resources.ok

@Composable
internal fun PermissionsShowAccessRequestDialog(
    state: PermissionsState,
    permissionLauncher: ActivityResultLauncher<Array<String>> = rememberPermissionFlowRequestLauncher(),
) {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    YesNoDialog(
        title = stringResource(string.missing_permissions),
        onNo = { state.showAccessRequest?.value = false },
        onYes = {
            state.showAccessRequest?.value = false
            state.missingPermissions?.let {
                permissionLauncher.launch(it.toTypedArray())
            }
        },
        yes = stringResource(string.ok),
        no = stringResource(string.cancel),
    ) {
        Text(stringResource(string.need_permissions_to_manage_gallery))
        UhuruCollapsibleGroup(
            groupState = rememberCollapsibleGroupState(
                title = string.having_problems,
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
                    painter = painterResource(id = drawable.ic_settings),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(string.navigate_to_settings))
            }
        }
        AlertText(
            text = stringResource(string.local_media_scan_warning)
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