package com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class PermissionsState(
    internal val missingPermissions: List<String>?,
    internal var showRationale: MutableState<Boolean>,
    internal var showAccessRequest: MutableState<Boolean>,
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
                showRationale.value = true
            } else if (!it.allPermissionsGranted) {
                showAccessRequest.value = true
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
            PermissionsCheck(state.value)
            return state
        }
    }

}