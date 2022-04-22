package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.settings.view.state.SettingsState
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction
import com.savvasdalkitsis.librephotos.settings.viewmodel.SettingsAction.*
import com.savvasdalkitsis.librephotos.ui.view.BackNavButton
import com.savvasdalkitsis.librephotos.ui.view.CommonScaffold
import com.savvasdalkitsis.librephotos.ui.view.FullProgressBar

@Composable
fun Settings(
    state: SettingsState,
    action: (SettingsAction) -> Unit,
) {
    CommonScaffold(
        navigationIcon = { BackNavButton {
            action(NavigateBack)
        }},
        title = { Text(text = "Settings") }
    ) { contentPadding ->
        if (state.isLoading) {
            FullProgressBar()
        } else {
            Column(
                modifier = Modifier
                    .padding(top = contentPadding.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsGroup(title = "Disk cache") {
                    SettingsButtonRow(
                        text = "Currently used: ${state.diskCacheCurrent}mb",
                        buttonText = "Clear",
                        onClick = { action(ClearDiskCache) }
                    )
                    Divider()
                    SettingsSliderRow(
                        text = "Max limit: ${state.diskCacheMax}mb",
                        subtext = "(changes will take effect after restart)",
                        value = state.diskCacheMax.toFloat(),
                        range = 10f..2000f,
                        onValueChange = { action(ChangeDiskCache(it)) }
                    )
                }
                SettingsGroup(title = "Memory cache") {
                    SettingsButtonRow(
                        text = "Currently used: ${state.memCacheCurrent}mb",
                        buttonText = "Clear",
                        onClick = { action(ClearMemCache) }
                    )
                    Divider()
                    SettingsSliderRow(
                        text = "Max limit: ${state.memCacheMax}mb",
                        subtext = "(changes will take effect after restart)",
                        value = state.memCacheMax.toFloat(),
                        range = 10f..2000f,
                        onValueChange = { action(ChangeMemCache(it)) }
                    )
                }
                SettingsGroup(title = "Jobs") {
                    SettingsSliderRow(
                        text = "Full photo feed sync frequency: ${state.feedSyncFrequency ?: "-"} hour(s)",
                        value = state.feedSyncFrequency?.toFloat(),
                        range = 1f..(7*24f),
                        steps = 24 * 7,
                        onValueChange = { action(ChangingFeedSyncFrequency(it)) },
                        onValueChangeFinished = { action(FinaliseFeedSyncFrequencyChange) }
                    )
                }
            }
        }
    }
}