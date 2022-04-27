package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.settings.viewmodel.SettingsAction

@Composable
fun SettingsTextDropDownButtonRow(
    text: String,
    buttonText: String,
    action: (SettingsAction) -> Unit,
    dropDownItems: @Composable DropDownMenuScope.() -> Unit,
) = SettingsTextDropDownButtonRow(
    content = { Text(text)},
    buttonText = buttonText,
    action = action,
    dropDownItems = dropDownItems
)

@Composable
fun SettingsTextDropDownButtonRow(
    content: @Composable () -> Unit,
    buttonText: String,
    action: (SettingsAction) -> Unit,
    dropDownItems: @Composable DropDownMenuScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth(),
    ) {
       Box(
           modifier = Modifier
               .padding(8.dp)
               .weight(1f)
               .align(Alignment.CenterVertically)) {
           content()
       }
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Button(onClick = { expanded = true }) {
                Text(buttonText)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                with(object : DropDownMenuScope {
                    @Composable
                    override fun Item(itemContent: @Composable RowScope.() -> Unit, action: SettingsAction) {
                        DropdownMenuItem(onClick = {
                            action(action)
                            expanded = false
                        }) {
                            itemContent()
                        }
                    }
                }) {
                    dropDownItems()
                }
            }
        }
    }
}

interface DropDownMenuScope {
    @Composable
    fun Item(itemContent: @Composable RowScope.() -> Unit, action: SettingsAction)

    @Composable
    fun Item(text: String, action: SettingsAction) = Item({ Text(text) }, action)
}