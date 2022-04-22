package com.savvasdalkitsis.librephotos.settings.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import com.savvasdalkitsis.librephotos.navigation.navigationTarget
import com.savvasdalkitsis.librephotos.settings.view.Settings
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.LoadSettings
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsEffect
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsEffectHandler
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsViewModel
import javax.inject.Inject

class SettingsNavigationTarget @Inject constructor(
    private val settingsEffectHandler: SettingsEffectHandler,
) {

    fun NavGraphBuilder.create() {
        navigationTarget<SettingsState, SettingsEffect, SettingsAction, SettingsViewModel>(
            name = name,
            effects = settingsEffectHandler,
            initializer = { _, actions -> actions(LoadSettings) },
            createModel = { hiltViewModel() }
        ) { state, actions ->
            Settings(state, actions)
        }
    }

    companion object {
        const val name = "settings"
    }
}