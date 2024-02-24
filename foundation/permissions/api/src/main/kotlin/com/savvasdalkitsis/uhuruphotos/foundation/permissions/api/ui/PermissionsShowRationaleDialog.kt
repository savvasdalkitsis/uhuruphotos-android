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

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.YesNoDialog
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
internal fun PermissionsShowRationaleDialog(
    state: PermissionsState,
    permissionLauncher: ActivityResultLauncher<Array<String>> = rememberPermissionFlowRequestLauncher(),
) {
    YesNoDialog(
        title = stringResource(string.missing_permissions),
        onNo = { state.showRationale?.value = false },
        onYes = {
            state.showRationale?.value = false
            state.missingPermissions?.let {
                permissionLauncher.launch(it.toTypedArray())
            }
        },
        yes = stringResource(string.ok),
        no = stringResource(string.cancel),
    ) {
        Text(stringResource(string.need_permissions_to_manage_gallery))
    }
}

@Preview
@Composable
private fun PermissionsShowRationaleDialogPreview() {
    PreviewAppTheme {
        PermissionsShowRationaleDialog(PermissionsState(), FakeResultLauncher)
    }
}