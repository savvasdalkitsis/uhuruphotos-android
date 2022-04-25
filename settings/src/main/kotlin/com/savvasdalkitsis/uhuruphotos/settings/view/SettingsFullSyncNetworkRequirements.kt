package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.work.NetworkType
import androidx.work.NetworkType.*
import com.savvasdalkitsis.uhuruphotos.settings.view.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction.ChangeFullSyncNetworkRequirements

private val NetworkType?.friendlyName: String
    get() = when (this) {
        CONNECTED -> "Any connected"
        UNMETERED -> "Unmetered"
        NOT_ROAMING -> "Not roaming"
        METERED -> "Metered"
        else -> "-"
    }

@Composable
fun SettingsFullSyncNetworkRequirements(
    state: SettingsState,
    action: (SettingsAction) -> Unit
) {
    SettingsTextDropDownButtonRow(
        text = "Network requirements: ${state.fullSyncNetworkRequirement.friendlyName}",
        buttonText = "Change"
    ) { dismiss ->
        @Composable
        fun item(networkType: NetworkType) {
            DropdownMenuItem(onClick = {
                action(ChangeFullSyncNetworkRequirements(networkType))
                dismiss()
            }) {
                Text(networkType.friendlyName)
            }
        }
        item(CONNECTED)
        item(UNMETERED)
        item(METERED)
        item(NOT_ROAMING)
    }
}