package com.savvasdalkitsis.uhuruphotos.settings.viewmodel

sealed class SettingsEffect {
    data class ShowMessage(val message: String) : SettingsEffect()
    object NavigateBack : SettingsEffect()
}
