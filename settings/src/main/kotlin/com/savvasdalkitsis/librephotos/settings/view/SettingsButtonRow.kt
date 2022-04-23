package com.savvasdalkitsis.librephotos.settings.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButtonRow(
    enabled: Boolean = true,
    buttonText: String,
    onClick: (() -> Unit),
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
    ) {
        Button(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            enabled = enabled,
            onClick = onClick
        ) {
            Text(buttonText)
        }
    }
}