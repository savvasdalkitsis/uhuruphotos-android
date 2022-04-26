package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.ui.theme.ThemeMode

@Composable
fun ThemeRow(themeMode: ThemeMode) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painter = painterResource(themeMode.icon), contentDescription = null)
        Text(themeMode.friendlyName)
    }
}