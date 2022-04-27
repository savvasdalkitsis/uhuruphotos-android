package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsProgressIndicator(
    text: String,
    progress: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(modifier = Modifier.weight(1f), text = text)
            Text("$progress%")
        }
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = progress / 100f)
    }
}