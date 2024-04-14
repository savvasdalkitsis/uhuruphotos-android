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

import androidx.compose.runtime.Composable
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
internal fun PermissionsCheck(
    state: PermissionsState,
) {
    state.Compose()
    val permissionLauncher = rememberPermissionFlowRequestLauncher()

    if (state.showRationale?.value == true || state.showAccessRequest?.value == true) {
        PermissionsShowAccessRequestDialog(state, permissionLauncher)
    }
}