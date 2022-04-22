package com.savvasdalkitsis.librephotos.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    handler: SettingsHandler,
) :
    ViewModel(),
    ActionReceiverHost<SettingsState, SettingsEffect, SettingsAction, SettingsMutation> {

    override val initialState = SettingsState()

    override val actionReceiver = ActionReceiver(
        handler,
        settingsReducer(),
        container(initialState)
    )
}