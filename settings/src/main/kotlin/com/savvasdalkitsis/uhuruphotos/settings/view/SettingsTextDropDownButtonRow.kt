/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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