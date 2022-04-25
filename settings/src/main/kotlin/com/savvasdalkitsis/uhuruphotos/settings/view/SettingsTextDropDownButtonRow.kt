package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTextDropDownButtonRow(
    text: String,
    buttonText: String,
    dropDownItems: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterStart),
            text = text,
        )
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterEnd)
        ) {
            Button(onClick = { expanded = true }) {
                Text(buttonText)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                dropDownItems {
                    expanded = false
                }
            }
        }
    }
}