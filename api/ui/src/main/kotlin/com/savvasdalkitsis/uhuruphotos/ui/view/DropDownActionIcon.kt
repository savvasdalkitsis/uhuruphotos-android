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
package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun DropDownActionIcon(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String? = null,
    items: @Composable DropDownActionIconScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ActionIcon(
            onClick = { expanded = true },
            icon = icon,
            contentDescription = contentDescription,
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val scope = DropDownActionIconScope()
            items(scope)
            with(scope) {
                for ((item, action) in itemsActions) {
                    DropdownMenuItem(onClick = {
                        action()
                        expanded = false
                    }) {
                        item()
                    }
                }
            }
        }
    }
}

class DropDownActionIconScope {
    val itemsActions: MutableList<Pair<@Composable RowScope.() -> Unit, () -> Unit>> = mutableListOf()

    @Composable
    fun item(itemContent: @Composable RowScope.() -> Unit, action: () -> Unit) {
        itemsActions += itemContent to action
    }

    @Composable
    fun item(text: String, action: () -> Unit) = item({ Text(text) }, action)
}
