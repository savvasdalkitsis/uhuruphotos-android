package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme

@Composable
internal fun PreviewTheme(theme: Theme, themeMode: ThemeMode, themeContrast: ThemeContrast) {
    PreviewAppTheme(
        theme = theme,
        themeMode = themeMode,
        themeContrast = themeContrast,
    ) {
        Card(
            modifier = Modifier.padding(12.dp),
            shape = CardDefaults.elevatedShape,
            colors = CardDefaults.elevatedCardColors(),
            elevation = CardDefaults.elevatedCardElevation(),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button({}) { Text("Button") }
                OutlinedButton({}) { Text("Button") }
                Checkbox(checked = false, onCheckedChange = {})
                Checkbox(checked = true, onCheckedChange = {})
            }
        }
    }
}