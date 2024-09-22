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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class PermissionsState(
    internal val missingPermissions: List<String>? = null,
    internal var showRationale: MutableState<Boolean>? = null,
    internal var showAccessRequest: MutableState<Boolean>? = null,
) {
    private var permissionState: MultiplePermissionsState? = null

    @Composable
    internal fun Compose() {
        permissionState = missingPermissions?.let {
            rememberMultiplePermissionsState(it)
        }
    }

    fun askForPermissions() {
        permissionState?.let {
            if (it.shouldShowRationale) {
                showRationale?.value = true
            } else if (!it.allPermissionsGranted) {
                showAccessRequest?.value = true
            }
        }
    }

    companion object {

        @Composable
        fun rememberPermissionsState(
            missingPermissions: List<String>?,
        ): MutableState<PermissionsState> {
            val state = remember(missingPermissions) {
                mutableStateOf(
                    PermissionsState(
                        missingPermissions,
                        mutableStateOf(false),
                        mutableStateOf(false),
                    )
                )
            }
            if (!LocalInspectionMode.current) {
                PermissionsCheck(state.value)
            }
            return state
        }
    }

}